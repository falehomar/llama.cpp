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

// Forward declaration if server_grammar_trigger is complex and defined elsewhere,
// or ensure its full definition is available if it's simple enough to be here or in an included header.
// For now, assuming server_grammar_trigger is resolvable via includes.
// struct server_grammar_trigger; // Example if it were needed

using json = nlohmann::ordered_json;

// Assuming server_tokens is a type alias, e.g., using server_tokens = std::vector<llama_token>;
// If it's more complex, its definition needs to be accessible.
// For now, let's assume it's defined in one of the included headers like "common.h" or "llama.h"
// or it's a simple typedef like:
using server_tokens = std::vector<llama_token>;


//######################################################################################################
//# REGION: ENUMS AND FORWARD DECLARATIONS
//######################################################################################################

/**
 * @brief Interval in seconds for HTTP polling
 */
constexpr int HTTP_POLLING_SECONDS = 1;

/**
 * @brief Types of stopping conditions for text generation
 */
enum stop_type {
    STOP_TYPE_NONE,    /**< No stopping condition met */
    STOP_TYPE_EOS,     /**< End of sequence token was generated */
    STOP_TYPE_WORD,    /**< A specific word/token stopping condition was met */
    STOP_TYPE_LIMIT,   /**< Token generation limit was reached */
};

// state diagram: https://github.com/ggml-org/llama.cpp/pull/9283
/**
 * @brief States of a server processing slot
 *
 * Represents the different states a slot can be in during text generation.
 * See state diagram: https://github.com/ggml-org/llama.cpp/pull/9283
 */
enum slot_state {
    SLOT_STATE_IDLE,              /**< Slot is idle and available for new tasks */
    SLOT_STATE_STARTED,           /**< Task has been started (initial setup) */
    SLOT_STATE_PROCESSING_PROMPT, /**< Slot is processing the input prompt */
    SLOT_STATE_DONE_PROMPT,       /**< Prompt processing completed, ready for generation */
    SLOT_STATE_GENERATING,        /**< Currently generating text tokens */
};

/**
 * @brief Current state of the server
 */
enum server_state {
    SERVER_STATE_LOADING_MODEL,  /**< Server is starting up, model not fully loaded yet */
    SERVER_STATE_READY,          /**< Server is ready and model is loaded */
};

/**
 * @brief Types of tasks that can be executed by the server
 *
 * These task types define the different operations that the server can perform,
 * such as text completion, embedding generation, etc.
 */
enum server_task_type {
    SERVER_TASK_TYPE_COMPLETION,   /**< Generate text completion */
    SERVER_TASK_TYPE_EMBEDDING,    /**< Generate embeddings for input text */
    SERVER_TASK_TYPE_RERANK,       /**< Re-rank candidate responses */
    SERVER_TASK_TYPE_INFILL,       /**< Fill in text based on context */
    SERVER_TASK_TYPE_CANCEL,       /**< Cancel an ongoing task */
    SERVER_TASK_TYPE_NEXT_RESPONSE,/**< Get the next response for an ongoing task */
    SERVER_TASK_TYPE_METRICS,      /**< Get server metrics */
    SERVER_TASK_TYPE_SLOT_SAVE,    /**< Save a slot's state to disk */
    SERVER_TASK_TYPE_SLOT_RESTORE, /**< Restore a slot's state from disk */
    SERVER_TASK_TYPE_SLOT_ERASE,   /**< Erase a slot's state */
    SERVER_TASK_TYPE_SET_LORA,     /**< Configure LoRA adapters */
};

/**
 * @brief OpenAI API compatibility modes
 *
 * These modes determine the format of responses to match OpenAI API standards
 */
enum oaicompat_type {
    OAICOMPAT_TYPE_NONE,       /**< No OpenAI compatibility */
    OAICOMPAT_TYPE_CHAT,       /**< ChatGPT-like completion */
    OAICOMPAT_TYPE_COMPLETION, /**< Text completion */
    OAICOMPAT_TYPE_EMBEDDING,  /**< Embedding generation */
};

// https://community.openai.com/t/openai-chat-list-of-error-codes-and-types/357791/11
/**
 * @brief Error types for response messages
 *
 * These error types are used to categorize different kinds of failures
 * in the server's API responses
 */
enum error_type {
    ERROR_TYPE_INVALID_REQUEST, /**< Invalid request parameters */
    ERROR_TYPE_AUTHENTICATION,  /**< Authentication failure */
    ERROR_TYPE_SERVER,          /**< Internal server error */
    ERROR_TYPE_NOT_FOUND,       /**< Resource not found */
    ERROR_TYPE_PERMISSION,      /**< Permission denied */
    ERROR_TYPE_UNAVAILABLE,     /**< Service unavailable (custom error) */
    ERROR_TYPE_NOT_SUPPORTED,   /**< Feature not supported (custom error) */
};


//######################################################################################################
//# DATA STRUCTURES
//######################################################################################################

// Helper function from common.h or similar, needed for slot_params::to_json
// We need to ensure format_logit_bias is available.
// It might be better to call this from a .cpp file if it's complex.
// For now, assuming it's a simple inline or static function in common.h or accessible.
// If not, this part of to_json might need to be refactored or the function moved.
// static inline json format_logit_bias(const std::map<llama_token, float>& biases) { ... }


/**
 * @brief Parameters for controlling the behavior of a server slot
 *
 * This structure defines configuration parameters for a slot's processing behavior,
 * including text generation settings, sampling parameters, and other options.
 */
struct slot_params {
    bool stream        = true;      /**< Whether to stream partial results as they are generated */
    bool cache_prompt  = true;      /**< Remember the prompt to avoid reprocessing all prompt tokens */
    bool return_tokens = false;     /**< Include token IDs in the response */

    int32_t n_keep    =  0; // number of tokens to keep from initial prompt
    int32_t n_discard =  0; // number of tokens after n_keep that may be discarded when shifting context, 0 defaults to half
    int32_t n_predict = -1; // new tokens to predict
    int32_t n_indent  =  0; // mininum line indentation for the generated text in number of whitespace characters

    int64_t t_max_prompt_ms  = -1; // TODO: implement
    int64_t t_max_predict_ms = -1; // if positive, limit the generation phase to this time limit

    std::vector<common_adapter_lora_info> lora;

    std::vector<std::string> antiprompt;
    std::vector<std::string> response_fields;
    bool timings_per_token = false;
    bool post_sampling_probs = false;
    bool ignore_eos = false; // This was in sampling, ensure it's correctly placed or handled

    struct common_params_sampling sampling;
    struct common_params_speculative speculative;

    // OAI-compat fields
    bool                         verbose                   = false;
    oaicompat_type               oaicompat                 = OAICOMPAT_TYPE_NONE;
    std::string                  oaicompat_model;
    std::string                  oaicompat_cmpl_id;
    common_chat_syntax           oaicompat_chat_syntax;

    json to_json() const {
        std::vector<std::string> samplers;
        samplers.reserve(sampling.samplers.size());
        for (const auto & sampler : sampling.samplers) {
            samplers.emplace_back(common_sampler_type_to_str(sampler));
        }

        json _lora = json::array();
        for (size_t i = 0; i < this->lora.size(); ++i) {
            _lora.push_back({{"id", i}, {"scale", this->lora[i].scale}});
        }

        auto grammar_triggers = json::array();
        // Assuming server_grammar_trigger has a to_json method and is constructible from sampling.grammar_triggers elements
        // This might require server_grammar_trigger definition if not already included.
        // For now, this is a placeholder if server_grammar_trigger is not fully defined/available.
        // If server_grammar_trigger is defined in json-schema-to-grammar.h and has to_json:
        for (const auto & trigger : sampling.grammar_triggers) {
             // server_grammar_trigger ct(std::move(trigger)); // This line might cause issues if trigger is const&
             // Let's assume trigger is copyable or movable, or server_grammar_trigger can take const ref
             server_grammar_trigger ct(trigger); // Or appropriate constructor
             grammar_triggers.push_back(ct.to_json());
        }


        return json {
            {"n_predict",                 n_predict},
            {"seed",                      sampling.seed},
            {"temperature",               sampling.temp},
            {"dynatemp_range",            sampling.dynatemp_range},
            {"dynatemp_exponent",         sampling.dynatemp_exponent},
            {"top_k",                     sampling.top_k},
            {"top_p",                     sampling.top_p},
            {"min_p",                     sampling.min_p},
            {"top_n_sigma",               sampling.top_n_sigma},
            {"xtc_probability",           sampling.xtc_probability},
            {"xtc_threshold",             sampling.xtc_threshold},
            {"typical_p",                 sampling.typ_p},
            {"repeat_last_n",             sampling.penalty_last_n},
            {"repeat_penalty",            sampling.penalty_repeat},
            {"presence_penalty",          sampling.penalty_present},
            {"frequency_penalty",         sampling.penalty_freq},
            {"dry_multiplier",            sampling.dry_multiplier},
            {"dry_base",                  sampling.dry_base},
            {"dry_allowed_length",        sampling.dry_allowed_length},
            {"dry_penalty_last_n",        sampling.dry_penalty_last_n},
            {"dry_sequence_breakers",     sampling.dry_sequence_breakers},
            {"mirostat",                  sampling.mirostat},
            {"mirostat_tau",              sampling.mirostat_tau},
            {"mirostat_eta",              sampling.mirostat_eta},
            {"stop",                      antiprompt},
            {"max_tokens",                n_predict},
            {"n_keep",                    n_keep},
            {"n_discard",                 n_discard},
            {"ignore_eos",                sampling.ignore_eos}, // sampling.ignore_eos was used in server.cpp
            {"stream",                    stream},
            {"logit_bias",                common_format_logit_bias(sampling.logit_bias)}, // Assuming common_format_logit_bias from common.h
            {"n_probs",                   sampling.n_probs},
            {"min_keep",                  sampling.min_keep},
            {"grammar",                   sampling.grammar},
            {"grammar_lazy",              sampling.grammar_lazy},
            {"grammar_triggers",          grammar_triggers},
            {"preserved_tokens",          sampling.preserved_tokens},
            {"chat_format",               common_chat_format_name(oaicompat_chat_syntax.format)},
            {"reasoning_format",          common_reasoning_format_name(oaicompat_chat_syntax.reasoning_format)},
            {"reasoning_in_content",      oaicompat_chat_syntax.reasoning_in_content},
            {"thinking_forced_open",      oaicompat_chat_syntax.thinking_forced_open},
            {"samplers",                  samplers},
            {"speculative.n_max",         speculative.n_max},
            {"speculative.n_min",         speculative.n_min},
            {"speculative.p_min",         speculative.p_min},
            {"timings_per_token",         timings_per_token},
            {"post_sampling_probs",       post_sampling_probs},
            {"lora",                      _lora},
        };
    }
};

/**
 * @brief Represents a task to be processed by the server
 *
 * A task encapsulates all the information needed to process a specific request,
 * including the task type, parameters, and any data required for processing.
 * Different task types use different fields within this structure.
 */
struct server_task {
    int id    = -1; /**< Task identifier, to be filled by server_queue */
    int index = -1; /**< Index used when there are multiple prompts in a batch request */

    server_task_type type; /**< Type of task to be performed */

    // used by SERVER_TASK_TYPE_CANCEL
    int id_target = -1; /**< Target task ID for cancellation tasks */

    // used by SERVER_TASK_TYPE_INFERENCE (and others like INFILL, EMBEDDING if they use similar params)
    slot_params   params;
    server_tokens prompt_tokens; // std::vector<llama_token>
    int id_selected_slot = -1;

    // used by SERVER_TASK_TYPE_SLOT_SAVE, SERVER_TASK_TYPE_SLOT_RESTORE, SERVER_TASK_TYPE_SLOT_ERASE
    /**
     * @brief Information for slot save/restore/erase operations
     */
    struct slot_action {
        int slot_id;            /**< The ID of the slot to operate on */
        std::string filename;   /**< Name of the file for save/restore operations */
        std::string filepath;   /**< Full path to the file for save/restore operations */
    };
    slot_action slot_action_params; // Renamed to avoid conflict with a potential member function named slot_action

    // used by SERVER_TASK_TYPE_METRICS
    bool metrics_reset_bucket = false;

    // used by SERVER_TASK_TYPE_SET_LORA
    std::vector<common_adapter_lora_info> set_lora;

    server_task(server_task_type type) : type(type) {}

    // It's generally better to move large static methods like this to a .cpp file
    // to reduce header inclusion burden and compile times.
    // For now, keeping it here as per original structure, but consider moving it later.
    static slot_params params_from_json_cmpl(
            const llama_context * ctx,
            const common_params & params_base,
            const json & data); // Implementation will be in a .cpp file or here if simple enough

    // utility function
    static std::unordered_set<int> get_list_id(const std::vector<server_task> & tasks) {
        std::unordered_set<int> ids;
        for (const auto & task : tasks) {
            ids.insert(task.id);
        }
        return ids;
    }
};


struct result_timings {
    int32_t prompt_n = -1;
    double prompt_ms = 0.0;
    double prompt_per_token_ms = 0.0;
    double prompt_per_second = 0.0;

    int32_t predicted_n = -1;
    double predicted_ms = 0.0;
    double predicted_per_token_ms = 0.0;
    double predicted_per_second = 0.0;

    // Optional speculative metrics - only included when > 0
    int32_t draft_n = 0;
    int32_t draft_n_accepted = 0;

    json to_json() const {
        json res = {
            {"prompt_n",             prompt_n},
            {"prompt_ms",            prompt_ms},
            {"prompt_per_token_ms",  prompt_per_token_ms},
            {"prompt_per_second",    prompt_per_second},
            {"predicted_n",          predicted_n},
            {"predicted_ms",         predicted_ms},
            {"predicted_per_token_ms", predicted_per_token_ms},
            {"predicted_per_second", predicted_per_second},
        };
        if (draft_n > 0) {
            res["draft_n"] = draft_n;
            res["draft_n_accepted"] = draft_n_accepted;
        }
        return res;
    }
};

/**
 * @brief Base class for all server task results
 *
 * This abstract class serves as the foundation for all types of task results
 * that can be returned by the server. It provides a common interface for
 * serializing results to JSON and checking their status.
 */
struct server_task_result {
    int id           = -1;  /**< Task identifier this result belongs to */
    int id_slot      = -1;  /**< Slot identifier that processed the task */

    /**
     * @brief Check if the result represents an error
     * @return true if this is an error result, false otherwise
     */
    virtual bool is_error() { return false; } // Default implementation

    /**
     * @brief Check if the result indicates a completed or stopped task
     * @return true if the task is stopped/completed, false otherwise
     */
    virtual bool is_stop() { return false; } // Default implementation

    /**
     * @brief Get the index for batch requests
     * @return The index within a batch, or -1 if not applicable
     */
    virtual int get_index() { return -1; } // Default implementation

    /**
     * @brief Serialize the result to JSON format
     * @return JSON object containing the result data
     */
    virtual json to_json() = 0;

    /**
     * @brief Virtual destructor for proper cleanup of derived classes
     */
    virtual ~server_task_result() = default;
};

// using shared_ptr for polymorphism of server_task_result
using server_task_result_ptr = std::unique_ptr<server_task_result>;

inline std::string stop_type_to_str(stop_type type) {
    switch (type) {
        case STOP_TYPE_EOS:   return "eos";
        case STOP_TYPE_WORD:  return "word";
        case STOP_TYPE_LIMIT: return "limit";
        default:              return "none";
    }
}

/**
 * @brief Represents a generated token and its associated information
 *
 * This structure contains all information related to a generated token,
 * including its probability, text representation, and top alternative tokens.
 */
struct completion_token_output {
    llama_token tok;         /**< Token ID */
    float prob;              /**< Token probability */
    std::string text_to_send; /**< Text representation of the token */

    /**
     * @brief Information about a token and its probability
     *
     * Used for storing top alternative tokens and their probabilities
     */
    struct prob_info {
        llama_token tok;
        std::string txt;
        float prob;
    };
    std::vector<prob_info> probs; /**< List of most probable alternative tokens */

    /**
     * @brief Convert token probability information to JSON
     * @param post_sampling_probs Whether to use raw probabilities or log probabilities
     * @return JSON object with token probability information
     */
    json to_json(bool post_sampling_probs) const {
        json res = json{
            {"tok_str", text_to_send}
        };
        if (post_sampling_probs) {
            res["prob"] = prob;
        } else {
            res["logprob"] = logarithm(prob);
        }


        if (!probs.empty()) {
            json top_probs_json = json::array();
            for (const auto & p_info : probs) {
                json p_json = {
                    {"tok_str", p_info.txt}
                };
                if (post_sampling_probs) {
                    p_json["prob"] = p_info.prob;
                } else {
                    p_json["logprob"] = logarithm(p_info.prob);
                }
                top_probs_json.push_back(p_json);
            }
            res["top_logprobs"] = top_probs_json; // OAI uses "top_logprobs"
        }
        return res;
    }

    static json probs_vector_to_json(const std::vector<completion_token_output> & probs_arg, bool post_sampling_probs) {
        json res = json::array();
        for (const auto & p : probs_arg) {
            res.push_back(p.to_json(post_sampling_probs));
        }
        return res;
    }


    static float logarithm(float x) {
        if (x == 0.0f) {
            return -std::numeric_limits<float>::infinity();
        }
        return std::log(x);
    }

    // This function seems out of place for completion_token_output, maybe a general utility?
    // For now, keeping it here as it was in the original context.
    static std::vector<unsigned char> str_to_bytes(const std::string & str) {
        return std::vector<unsigned char>(str.begin(), str.end());
    }
};

/**
 * @brief Final result for a text completion task
 *
 * Contains the complete generated text and associated metadata for a
 * completed text generation task. This is returned when a generation
 * task has finished.
 */
struct server_task_result_cmpl_final : server_task_result {
    int index = 0;               /**< Index for batch requests */

    std::string content;         /**< The generated text content */
    llama_tokens tokens;         /**< List of token IDs that were generated */

    bool stream;                 /**< Whether this result is part of a stream */
    result_timings timings;      /**< Performance timing information */
    std::string prompt;          /**< The input prompt that produced this result */

    bool truncated = false;      /**< Whether the result was truncated */
    int32_t n_decoded = 0;
    int32_t n_prompt_tokens = 0;
    int32_t n_tokens_cached = 0;
    bool has_new_line = false;
    std::string stopping_word;
    stop_type stop = STOP_TYPE_NONE;
    bool post_sampling_probs = false;
    std::vector<completion_token_output> probs_output;
    std::vector<std::string> response_fields; // Fields to include in the response
    slot_params generation_params; // Parameters used for this generation

    // OAI-compat fields
    bool               verbose                  = false;
    oaicompat_type     oaicompat                = OAICOMPAT_TYPE_NONE;
    std::string        oaicompat_model;
    std::string        oaicompat_cmpl_id;
    common_chat_msg    oaicompat_msg; // For chat completions, the message
    std::vector<common_chat_msg_diff> oaicompat_msg_diffs; // For streaming chat diffs

    // Implementations for virtual functions from server_task_result
    bool is_stop() override { return stop != STOP_TYPE_NONE || truncated; }
    int get_index() override { return index; }

    // The to_json method for this struct can be quite large and might also be a candidate
    // for moving to a .cpp file. For now, it's a declaration.
    json to_json() override; // Implementation will be in a .cpp file
};

// Declaration for the static method moved from server_task
// The definition should be in a .cpp file (e.g., server_task.cpp or server_utils.cpp)
// slot_params server_task::params_from_json_cmpl(
//         const llama_context * ctx,
//         const common_params & params_base,
//         const json & data);

// Declaration for the to_json method of server_task_result_cmpl_final
// The definition should be in a .cpp file (e.g., server_task_result.cpp or server_utils.cpp)
// json server_task_result_cmpl_final::to_json();


#endif // SERVER_TYPES_H
