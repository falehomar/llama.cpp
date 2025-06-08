#ifndef SERVER_TYPES_H
#define SERVER_TYPES_H

#include "common.h" // For common_params, common_adapter_lora_info, common_chat_syntax, etc.
#include "llama.h"  // For llama_model, llama_context, llama_token, llama_vocab, etc.
#include "sampling.h" // For common_params_sampling
#include "chat.h"     // For common_chat_msg, common_chat_msg_diff
#include "json-schema-to-grammar.h" // For server_grammar_trigger if it's defined here, or adjust as needed

#include <string>
#include <vector>
#include <memory> // For std::unique_ptr
#include <unordered_map> // For format_logit_bias potentially
#include <nlohmann/ordered_json.hpp> // For json
#include <deque>
#include <mutex>
#include <condition_variable>
#include <atomic> // For std::atomic_bool in server_context potentially

// Forward declarations from llama.h if not fully included, ensure llama.h is included for full types
struct llama_model;
struct llama_context;
struct llama_batch;
struct llama_grammar;
struct llama_clip_ctx; // For multimodal

// Server-wide parameters
struct server_params {
    std::string hostname = "127.0.0.1";
    std::vector<int> port = {8080};
    std::string public_path = "examples/server/public"; // Path to static files
    int32_t n_threads_http = -1; // Max number of threads for the HTTP server
    std::string api_key;
    std::string api_key_path;
    bool verbose_http = false;

    // Model loading related, but distinct from common_params
    std::string model_draft_path; // Path to draft model for speculative decoding
    // ... other server-specific parameters from the original server_params in server.cpp
    bool slots_endpoint = true; // Enable /slots endpoint
    bool metrics_endpoint = true; // Enable /metrics endpoint
    bool disable_log = false;
    bool log_json = false;
    bool slot_save_load = false;
    std::string slot_path_template = "slots/slot_{id}.json"; // Example
};

// Represents a processing slot on the server
struct server_slot {
    int id;
    slot_state state = SLOT_STATE_IDLE;
    slot_params params; // Parameters for the current task in this slot

    server_tokens prompt_tokens;
    std::string prompt_text; // Keep original prompt text for logging or other purposes
    json request_json; // Store the original request json for this slot

    int32_t n_ctx; // Context size for this slot's llama_context
    llama_batch batch = { 0, nullptr, nullptr, nullptr, nullptr, nullptr, nullptr, nullptr, 0, 0, 0, }; // Batch for this slot
    llama_context * ctx = nullptr; // llama_context specific to this slot if per-slot contexts are used, or references main context
                                   // If using a single main context, this might not be needed here or managed differently.
                                   // The original server.cpp seems to use one main context and manages sequences within it.

    int task_id = -1;
    int task_index = 0; // for batched requests

    int64_t t_start_process_prompt;
    int64_t t_start_generation;

    // Output
    std::string generated_text;
    std::vector<completion_token_output> generated_token_probs;
    result_timings timings;
    stop_type stopping_reason = STOP_TYPE_NONE;
    std::string stopping_word;
    bool truncated = false;
    bool has_new_line = false; // For infill formatting

    // For speculative decoding state if managed per slot
    struct llama_sampling_context * ctx_sampling = nullptr;

    // Methods
    server_slot(int id, int32_t n_ctx_slot); // Constructor
    ~server_slot(); // Destructor to clean up slot-specific resources like ctx_sampling, batch

    json to_json_metrics() const; // For /slots endpoint
    void reset(); // Reset slot to initial state
    // bool load_state(const std::string & filename, const llama_vocab * vocab);
    // bool save_state(const std::string & filename) const;
};

// Server metrics
struct server_metrics {
    std::chrono::time_point<std::chrono::high_resolution_clock> t_start;

    // Bucket-based metrics (e.g., per minute, per 5 minutes)
    // This is a simplified representation; the original might be more complex
    int n_prompt_tokens_processed_total = 0;
    int n_tokens_predicted_total = 0;
    // ... other metrics fields

    server_metrics();
    json to_json() const;
    void reset_bucket(int bucket_idx); // Placeholder
    void on_prompt_eval(const server_slot& slot);
    void on_token_predict(const server_slot& slot);
};

// Task queue for the server
struct server_task_queue {
    std::deque<server_task> tasks;
    std::mutex mutex_tasks;
    std::condition_variable cv_tasks;
    std::atomic<int> next_task_id; // Ensure thread-safe ID generation

    server_task_queue() : next_task_id(0) {}

    void add_task(server_task & task);
    bool get_task_to_process(server_task & task); // Gets a task for a slot to process
    void complete_task(int task_id, server_task_result_ptr result); // For results
    std::vector<server_task_result_ptr> get_completed_tasks_results(int last_id, int & new_last_id, bool wait);
    void cancel_task(int task_id);
    json get_all_tasks_status() const; // For /tasks endpoint if added
private:
    std::deque<server_task_result_ptr> completed_tasks;
    std::mutex mutex_completed_tasks;
    std::condition_variable cv_completed_tasks;
    int next_result_id_served = -1;
};

// Central context for the server, holding shared resources and state
struct server_context {
    llama_model * model = nullptr;
    llama_context * ctx = nullptr; // Main llama_context

    common_params params_base; // Parsed from command line, common to all tasks unless overridden
    server_params sparams;     // Server-specific parameters

    std::vector<server_slot> slots;
    server_task_queue task_queue;
    server_metrics metrics;

    bool multimodal = false;
    llama_clip_ctx * clip_ctx = nullptr;

    // System prompt related
    std::string system_prompt_text;
    std::string system_prompt_output_template; // For chat templates
    server_tokens system_prompt_tokens;
    llama_grammar * system_prompt_grammar = nullptr; // Grammar for system prompt / chat templates

    std::atomic<bool> running; // To control server main loop

    // Constructor and Destructor
    server_context() : running(true) {}
    ~server_context(); // Definition in .cpp to handle resource cleanup (model, ctx, etc.)
};

// Make sure to include nlohmann/json.hpp if it's not already transitively included
// #include <nlohmann/ordered_json.hpp>
// using json = nlohmann::ordered_json; // This is usually at the top or in a common header

#endif // SERVER_TYPES_H
