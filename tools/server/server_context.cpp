#include "server_types.h"
#include "llama.h"
#include "common.h"
#include "log.h"
#include <stdexcept> // For std::runtime_error
#include <fstream>   // For file operations in slot save/load (if implemented here)

// server_slot method definitions
server_slot::server_slot(int slot_id, int32_t n_ctx_slot) : id(slot_id), n_ctx(n_ctx_slot) {
    // Initialize batch for this slot
    // The size of the batch buffer, e.g., n_ctx_slot, or a common max batch size
    // This needs to align with how llama_batch_init is called in the main server logic
    // For now, just zero-initializing. Actual llama_batch_init will be elsewhere.
    batch = llama_batch_init(n_ctx_slot, 0, 1); // Assuming 1 sequence per batch for a slot
    ctx_sampling = llama_sampling_init(params.sampling); // Initialize with default params, will be updated per task
}

server_slot::~server_slot() {
    if (batch.token) {
        llama_batch_free(batch);
    }
    if (ctx_sampling) {
        llama_sampling_free(ctx_sampling);
    }
    // If this slot had its own llama_context, it should be freed here.
    // However, the original server.cpp uses a shared context for all slots.
}

void server_slot::reset() {
    state = SLOT_STATE_IDLE;
    // params = slot_params(); // Reset to default, or specific defaults
    prompt_tokens.clear();
    prompt_text.clear();
    request_json = json::object();
    task_id = -1;
    task_index = 0;
    generated_text.clear();
    generated_token_probs.clear();
    // timings = result_timings(); // Reset timings
    stopping_reason = STOP_TYPE_NONE;
    stopping_word.clear();
    truncated = false;
    has_new_line = false;

    if (ctx_sampling) {
        llama_sampling_reset(ctx_sampling);
    }
    // llama_kv_cache_clear for this slot's sequence ID if using shared context
    // Or if this slot has its own context, llama_kv_cache_clear(ctx);
}

json server_slot::to_json_metrics() const {
    return json {
        {"id", id},
        {"task_id", task_id},
        {"state", static_cast<int>(state)}, // Consider converting enum to string
        {"prompt_tokens", prompt_tokens.size()},
        {"generated_tokens", generated_token_probs.size()},
        {"params", params.to_json()} // Assuming params.to_json() exists
    };
}

// server_metrics method definitions
server_metrics::server_metrics() : t_start(std::chrono::high_resolution_clock::now()) {
    // Initialize any metrics structures
}

json server_metrics::to_json() const {
    // Placeholder for actual metrics JSON structure
    return json {
        {"uptime_s", std::chrono::duration_cast<std::chrono::seconds>(std::chrono::high_resolution_clock::now() - t_start).count()},
        {"n_prompt_tokens_processed_total", n_prompt_tokens_processed_total},
        {"n_tokens_predicted_total", n_tokens_predicted_total}
        // ... other metrics
    };
}

void server_metrics::reset_bucket(int bucket_idx) {
    // Placeholder for resetting bucketed metrics
    (void)bucket_idx; // Unused for now
}

void server_metrics::on_prompt_eval(const server_slot& slot) {
    n_prompt_tokens_processed_total += slot.prompt_tokens.size();
    // Update other relevant metrics
}

void server_metrics::on_token_predict(const server_slot& slot) {
    n_tokens_predicted_total += 1; // Assuming called per token
    // Update other relevant metrics
}


// server_task_queue method definitions
void server_task_queue::add_task(server_task & task) {
    std::lock_guard<std::mutex> lock(mutex_tasks);
    task.id = next_task_id++;
    tasks.push_back(task);
    cv_tasks.notify_one();
}

bool server_task_queue::get_task_to_process(server_task & task) {
    std::unique_lock<std::mutex> lock(mutex_tasks);
    cv_tasks.wait(lock, [this]{ return !tasks.empty(); });
    if (tasks.empty()) {
        return false; // Should not happen with cv_tasks
    }
    task = tasks.front();
    tasks.pop_front();
    return true;
}

void server_task_queue::complete_task(int task_id, server_task_result_ptr result) {
    std::lock_guard<std::mutex> lock(mutex_completed_tasks);
    result->id = task_id; // Ensure result has the correct task ID
    completed_tasks.push_back(std::move(result));
    cv_completed_tasks.notify_all();
}

std::vector<server_task_result_ptr> server_task_queue::get_completed_tasks_results(int last_id, int & new_last_id, bool wait) {
    std::vector<server_task_result_ptr> results_to_send;
    std::unique_lock<std::mutex> lock(mutex_completed_tasks);

    if (wait) {
        cv_completed_tasks.wait(lock, [this, last_id] {
            for (const auto& res_ptr : completed_tasks) {
                if (res_ptr->id > last_id) return true;
            }
            return false; // No new results yet
        });
    }

    int current_max_id = last_id;
    // Iterate and collect results with ID > last_id
    // This is a simplified approach; the original server might have a more complex way to track served results.
    // A more robust way would be to remove served results or use a different structure.
    for (auto it = completed_tasks.begin(); it != completed_tasks.end(); /* no increment here */) {
        if ((*it)->id > last_id) {
            // This logic needs to be careful about ownership if results are moved out.
            // For now, let's assume we are just peeking or the client handles duplicates/order.
            // A better approach for HTTP long polling is to send only new results and update last_id.
            // The original server.cpp has a more complex result handling for streaming.
            // This is a simplified version for non-streaming or batch results.

            // To avoid sending the same result multiple times if client polls with old last_id,
            // we should ideally remove them or mark as served.
            // For this example, let's assume results are moved out.
            results_to_send.push_back(std::move(*it)); // Move result to the send queue
            it = completed_tasks.erase(it); // Remove from completed_tasks
            if (!results_to_send.empty()){
                 current_max_id = std::max(current_max_id, results_to_send.back()->id);
            }
        } else {
            ++it;
        }
    }
    new_last_id = current_max_id;
    return results_to_send;
}

void server_task_queue::cancel_task(int task_id_to_cancel) {
    // Cancellation logic can be complex:
    // 1. Remove from pending queue if not started.
    // 2. Signal running slot to stop if already processing.
    // This is a simplified version.
    std::lock_guard<std::mutex> lock(mutex_tasks);
    for (auto it = tasks.begin(); it != tasks.end(); ++it) {
        if (it->id == task_id_to_cancel) {
            tasks.erase(it);
            // Optionally, add a "cancelled" result to completed_tasks
            return;
        }
    }
    // If not in queue, it might be running. The slot itself would need to handle cancellation.
}

json server_task_queue::get_all_tasks_status() const {
    json tasks_array = json::array();
    {
        std::lock_guard<std::mutex> lock(mutex_tasks);
        for(const auto& task : tasks) {
            tasks_array.push_back({{"id", task.id}, {"status", "pending"}});
        }
    }
    // Could also iterate completed_tasks for recent ones, or slots for active ones.
    return tasks_array;
}

// server_context method definitions
server_context::~server_context() {
    running = false; // Signal all threads to stop

    if (system_prompt_grammar) {
        llama_grammar_free(system_prompt_grammar);
    }
    if (clip_ctx) {
        llama_clip_free(clip_ctx);
    }
    if (ctx) {
        llama_free(ctx);
    }
    if (model) {
        llama_free_model(model);
    }
    llama_backend_free();
}

// Any other definitions from server_content.cpp that are specific to these new structs
// (e.g. methods of server_params if it had any) would go here.
