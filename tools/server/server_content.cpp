#include "server_types.h"
#include "utils.hpp" // For json_value and other utilities
#include "common.h"  // For common_params, common_chat_format_from_string, etc.
#include "llama.h"   // For llama_context, llama_model_get_vocab, etc.
#include "json-schema-to-grammar.h" // For json_schema_to_grammar, server_grammar_trigger
#include "log.h"     // For SRV_DBG, etc.

// Definition for server_task::params_from_json_cmpl
slot_params server_task::params_from_json_cmpl(
        const llama_context * ctx,
        const common_params & params_base,
        const json & data)
{
    const llama_model * model = llama_get_model(ctx);
    const llama_vocab * vocab = llama_model_get_vocab(model);

    slot_params params;

    // Sampling parameter defaults are loaded from the global server context
    slot_params defaults;
    defaults.sampling    = params_base.sampling;
    defaults.speculative = params_base.speculative;

    params.verbose           = params_base.verbosity > 9;
    params.timings_per_token = json_value(data, "timings_per_token", false);

    params.stream           = json_value(data, "stream",             false);
    params.cache_prompt     = json_value(data, "cache_prompt",       true);
    params.return_tokens    = json_value(data, "return_tokens",      false);
    params.n_predict        = json_value(data, "n_predict",          json_value(data, "max_tokens", defaults.n_predict));
    params.n_indent         = json_value(data, "n_indent",           defaults.n_indent);
    params.n_keep           = json_value(data, "n_keep",             defaults.n_keep);
    params.n_discard        = json_value(data, "n_discard",          defaults.n_discard);
    params.t_max_predict_ms = json_value(data, "t_max_predict_ms",   defaults.t_max_predict_ms);
    params.response_fields  = json_value(data, "response_fields",   std::vector<std::string>());

    params.sampling.top_k              = json_value(data, "top_k",              defaults.sampling.top_k);
    params.sampling.top_p              = json_value(data, "top_p",              defaults.sampling.top_p);
    params.sampling.min_p              = json_value(data, "min_p",              defaults.sampling.min_p);
    params.sampling.top_n_sigma        = json_value(data, "top_n_sigma",        defaults.sampling.top_n_sigma);
    params.sampling.xtc_probability    = json_value(data, "xtc_probability",    defaults.sampling.xtc_probability);
    params.sampling.xtc_threshold      = json_value(data, "xtc_threshold",      defaults.sampling.xtc_threshold);
    params.sampling.typ_p              = json_value(data, "typical_p",          defaults.sampling.typ_p);
    params.sampling.temp               = json_value(data, "temperature",        defaults.sampling.temp);
    params.sampling.dynatemp_range     = json_value(data, "dynatemp_range",     defaults.sampling.dynatemp_range);
    params.sampling.dynatemp_exponent  = json_value(data, "dynatemp_exponent",  defaults.sampling.dynatemp_exponent);
    params.sampling.penalty_last_n     = json_value(data, "repeat_last_n",      defaults.sampling.penalty_last_n);
    params.sampling.penalty_repeat     = json_value(data, "repeat_penalty",     defaults.sampling.penalty_repeat);
    params.sampling.penalty_freq       = json_value(data, "frequency_penalty",  defaults.sampling.penalty_freq);
    params.sampling.penalty_present    = json_value(data, "presence_penalty",   defaults.sampling.penalty_present);
    params.sampling.dry_multiplier     = json_value(data, "dry_multiplier",     defaults.sampling.dry_multiplier);
    params.sampling.dry_base           = json_value(data, "dry_base",           defaults.sampling.dry_base);
    params.sampling.dry_allowed_length = json_value(data, "dry_allowed_length", defaults.sampling.dry_allowed_length);
    params.sampling.dry_penalty_last_n = json_value(data, "dry_penalty_last_n", defaults.sampling.dry_penalty_last_n);
    params.sampling.mirostat           = json_value(data, "mirostat",           defaults.sampling.mirostat);
    params.sampling.mirostat_tau       = json_value(data, "mirostat_tau",       defaults.sampling.mirostat_tau);
    params.sampling.mirostat_eta       = json_value(data, "mirostat_eta",       defaults.sampling.mirostat_eta);
    params.sampling.seed               = json_value(data, "seed",               defaults.sampling.seed);
    params.sampling.n_probs            = json_value(data, "n_probs",            defaults.sampling.n_probs);
    params.sampling.min_keep           = json_value(data, "min_keep",           defaults.sampling.min_keep);
    params.post_sampling_probs         = json_value(data, "post_sampling_probs", defaults.post_sampling_probs);

    params.speculative.n_min = json_value(data, "speculative.n_min", defaults.speculative.n_min);
    params.speculative.n_max = json_value(data, "speculative.n_max", defaults.speculative.n_max);
    params.speculative.p_min = json_value(data, "speculative.p_min", defaults.speculative.p_min);

    params.speculative.n_min = std::min(params.speculative.n_max, params.speculative.n_min);
    params.speculative.n_min = std::max(params.speculative.n_min, 0);
    params.speculative.n_max = std::max(params.speculative.n_max, 0);

    if (data.contains("logprobs") && params.sampling.n_probs == defaults.sampling.n_probs){
        params.sampling.n_probs = json_value(data, "logprobs", defaults.sampling.n_probs);
    }

    if (data.contains("lora")) {
        if (data.at("lora").is_array()) {
            for (const auto & item : data.at("lora")) {
                common_adapter_lora_info lora_info;
                lora_info.path = json_value(item, "path", std::string("")); // Assuming path is a string
                lora_info.scale = json_value(item, "scale", 1.0f);
                params.lora.push_back(lora_info);
            }
        } else {
             // Handle non-array lora if necessary, or throw error
        }
    } else {
        params.lora = params_base.lora_adapters;
    }

    if (params.sampling.penalty_last_n < -1) {
        throw std::runtime_error("Error: repeat_last_n must be >= -1");
    }
    if (params.sampling.dry_penalty_last_n < -1) {
        throw std::runtime_error("Error: dry_penalty_last_n must be >= -1");
    }
    if (params.sampling.penalty_last_n == -1) {
        params.sampling.penalty_last_n = llama_n_ctx(ctx);
    }
    if (params.sampling.dry_penalty_last_n == -1) {
        params.sampling.dry_penalty_last_n = llama_n_ctx(ctx);
    }
    if (params.sampling.dry_base < 1.0f) {
        params.sampling.dry_base = defaults.sampling.dry_base;
    }

    if (data.contains("dry_sequence_breakers")) {
        params.sampling.dry_sequence_breakers = json_value(data, "dry_sequence_breakers", std::vector<std::string>());
        if (params.sampling.dry_sequence_breakers.empty()) {
             // Potentially log or handle empty sequence breakers if needed
        }
    }

    if (data.contains("json_schema") && !data.contains("grammar")) {
        try {
            const json schema = data.at("json_schema");
            params.sampling.grammar = json_schema_to_grammar(schema);
            SRV_DBG("Converted JSON schema to grammar: %s\n", params.sampling.grammar.c_str());
        } catch (const std::exception & e) {
            SRV_DBG("Failed to convert JSON schema to grammar: %s\n", e.what());
            throw std::runtime_error(std::string("Error: Failed to convert JSON schema to grammar: ") + e.what());
        }
    } else {
        params.sampling.grammar      = json_value(data, "grammar", defaults.sampling.grammar);
        SRV_DBG("Grammar: %s\n", params.sampling.grammar.c_str());
        params.sampling.grammar_lazy = json_value(data, "grammar_lazy", defaults.sampling.grammar_lazy);
    }

    auto it_chat_format = data.find("chat_format");
    if (it_chat_format != data.end()) {
        params.oaicompat_chat_syntax.format = common_chat_format_from_string(it_chat_format->get<std::string>());
    } else {
        params.oaicompat_chat_syntax.format = params_base.chat_format;
    }
    params.oaicompat_chat_syntax.reasoning_format = params_base.reasoning_format;
    params.oaicompat_chat_syntax.reasoning_in_content = params.stream && (params_base.reasoning_format == COMMON_REASONING_FORMAT_DEEPSEEK_LEGACY);
    params.oaicompat_chat_syntax.thinking_forced_open = json_value(data, "thinking_forced_open", false);
    params.oaicompat_chat_syntax.parse_tool_calls = json_value(data, "parse_tool_calls", false);

    const auto preserved_tokens_it = data.find("preserved_tokens");
    if (preserved_tokens_it != data.end()) {
        // Assuming preserved_tokens is an array of numbers (token IDs)
        for (const auto& token_id_json : *preserved_tokens_it) {
            if (token_id_json.is_number_integer()) {
                params.sampling.preserved_tokens.push_back(token_id_json.get<llama_token>());
            }
        }
    }
    const auto grammar_triggers_it = data.find("grammar_triggers");
    if (grammar_triggers_it != data.end()) {
        // Assuming grammar_triggers is an array of objects or strings based on server_grammar_trigger structure
        for (const auto& trigger_json : *grammar_triggers_it) {
            params.sampling.grammar_triggers.emplace_back(server_grammar_trigger::from_json(vocab, trigger_json));
        }
    }
    if (params.sampling.grammar_lazy && params.sampling.grammar_triggers.empty()) {
        SRV_DBG("Warning: grammar_lazy is true but no grammar_triggers are defined.\n");
    }

    params.sampling.logit_bias.clear();
    params.ignore_eos = json_value(data, "ignore_eos", false);

    const auto & logit_bias_it = data.find("logit_bias");
    if (logit_bias_it != data.end() && logit_bias_it->is_array()) {
        for (const auto & item : *logit_bias_it) {
            if (item.is_array() && item.size() == 2 && item[0].is_number_integer() && item[1].is_number()) {
                params.sampling.logit_bias[item[0].get<llama_token>()] = item[1].get<float>();
            }
        }
    }

    params.antiprompt.clear();
    const auto & stop_it = data.find("stop");
    if (stop_it != data.end() && stop_it->is_array()) {
        for (const auto & item : *stop_it) {
            if (item.is_string()) {
                params.antiprompt.push_back(item.get<std::string>());
            }
        }
    }

    const auto samplers_it = data.find("samplers");
    if (samplers_it != data.end()) {
        params.sampling.samplers.clear();
        for (const auto & sampler_str_json : *samplers_it) {
            if (sampler_str_json.is_string()) {
                enum llama_sampler_type sampler_type = common_sampler_type_from_str(sampler_str_json.get<std::string>().c_str());
                if (sampler_type != LLAMA_SAMPLER_TYPE_COUNT) { // Check if valid
                    params.sampling.samplers.push_back(sampler_type);
                }
            }
        }
    } else {
        // Use default samplers from params_base if not provided in request
        params.sampling.samplers = defaults.sampling.samplers;
    }
    // Default model name for OAI compatibility
    params.oaicompat_model = params_base.model_alias.empty() ? DEFAULT_OAICOMPAT_MODEL : params_base.model_alias;

    return params;
}

// Definition for server_task_result_cmpl_final::to_json
json server_task_result_cmpl_final::to_json() {
    json res_obj = json::object();
    json choices_arr = json::array();
    json choice_obj = json::object();

    std::string finish_reason_str = stop_type_to_str(stop);
    if (truncated) {
        finish_reason_str = "length"; // OpenAI uses "length" for max_tokens limit
    }

    if (oaicompat == OAICOMPAT_TYPE_CHAT) {
        choice_obj["finish_reason"] = finish_reason_str;
        choice_obj["index"] = index;

        json message_obj = json::object();
        if (oaicompat_msg.role.empty()) { // Streaming delta
            if (!oaicompat_msg_diffs.empty()) {
                // For streaming, build delta from diffs
                // This part might need more complex logic based on how diffs are structured
                // Assuming diffs provide content directly or tool calls
                std::string current_content;
                json tool_calls_arr = json::array();

                for(const auto& diff : oaicompat_msg_diffs) {
                    if (diff.type == common_chat_msg_diff::ADD_CONTENT) {
                        current_content += diff.str_value;
                    } else if (diff.type == common_chat_msg_diff::ADD_TOOL_CALL) {
                        // Assuming diff.str_value is a JSON string for the tool call part
                        // Or diff might have structured tool call info
                        // This is a simplified placeholder
                        try {
                            tool_calls_arr.push_back(json::parse(diff.str_value));
                        } catch (const json::parse_error& e) {
                            SRV_DBG("Failed to parse tool call JSON: %s\n", e.what());
                        }
                    }
                }
                if (!current_content.empty()) {
                    message_obj["content"] = current_content;
                }
                if (!tool_calls_arr.empty()) {
                    message_obj["tool_calls"] = tool_calls_arr;
                }
                // Role might be part of the diffs or set initially
                // For delta, role is usually assistant if content is present
                if (message_obj.contains("content") || message_obj.contains("tool_calls")) {
                     message_obj["role"] = "assistant";
                }

            }
            // If it's a final non-streaming chunk but still part of a stream response structure
            else if (!content.empty() || !oaicompat_msg.tool_calls.empty()) {
                 message_obj["role"] = "assistant"; // Default for assistant message
                 if (!content.empty()) {
                    message_obj["content"] = content;
                 }
                 if (!oaicompat_msg.tool_calls.empty()) {
                    json tc_array = json::array();
                    for(const auto& tc : oaicompat_msg.tool_calls) {
                        json tc_obj = {{"id", tc.id}, {"type", "function"}};
                        json func_obj = {{"name", tc.function.name}};
                        if (!tc.function.arguments.empty()) {
                            func_obj["arguments"] = tc.function.arguments;
                        }
                        tc_obj["function"] = func_obj;
                        tc_array.push_back(tc_obj);
                    }
                    message_obj["tool_calls"] = tc_array;
                 }
            }

        } else { // Non-streaming or final message part
            message_obj["role"] = oaicompat_msg.role;
            if (oaicompat_msg.content_type == COMMON_CHAT_MSG_CONTENT_TYPE_TEXT) {
                 message_obj["content"] = oaicompat_msg.content;
            } else if (oaicompat_msg.content_type == COMMON_CHAT_MSG_CONTENT_TYPE_TOOL_CALLS) {
                json tc_array = json::array();
                for(const auto& tc : oaicompat_msg.tool_calls) {
                    json tc_obj = {{"id", tc.id}, {"type", "function"}};
                    json func_obj = {{"name", tc.function.name}};
                    if (!tc.function.arguments.empty()) {
                        func_obj["arguments"] = tc.function.arguments;
                    }
                    tc_obj["function"] = func_obj;
                    tc_array.push_back(tc_obj);
                }
                message_obj["tool_calls"] = tc_array;
            }
        }
        choice_obj["message"] = message_obj;

        // Logprobs for chat completion (if requested)
        if (generation_params.sampling.n_probs > 0 && !probs_output.empty()) {
            json logprobs_obj = json::object();
            json content_arr = json::array();
            for (const auto & token_out : probs_output) {
                content_arr.push_back(token_out.to_json(post_sampling_probs));
            }
            logprobs_obj["content"] = content_arr;
            choice_obj["logprobs"] = logprobs_obj;
        }

    } else if (oaicompat == OAICOMPAT_TYPE_COMPLETION) {
        choice_obj["text"] = content;
        choice_obj["finish_reason"] = finish_reason_str;
        choice_obj["index"] = index;
        // Logprobs for text completion (if requested)
        if (generation_params.sampling.n_probs > 0 && !probs_output.empty()) {
            // OpenAI text completion logprobs format is a bit different
            json logprobs_data = json::object();
            json tokens_arr = json::array();
            json token_logprobs_arr = json::array();
            json top_logprobs_arr = json::array(); // Array of objects
            json text_offset_arr = json::array();
            int current_offset = 0;

            for (const auto & token_out : probs_output) {
                tokens_arr.push_back(token_out.text_to_send);
                token_logprobs_arr.push_back(completion_token_output::logarithm(token_out.prob));
                text_offset_arr.push_back(current_offset);
                current_offset += token_out.text_to_send.length();

                json top_probs_for_token = json::object();
                for (const auto & p_info : token_out.probs) {
                    top_probs_for_token[p_info.txt] = completion_token_output::logarithm(p_info.prob);
                }
                top_logprobs_arr.push_back(top_probs_for_token);
            }
            logprobs_data["tokens"] = tokens_arr;
            logprobs_data["token_logprobs"] = token_logprobs_arr;
            logprobs_data["top_logprobs"] = top_logprobs_arr;
            logprobs_data["text_offset"] = text_offset_arr;
            choice_obj["logprobs"] = logprobs_data;
        }
    } else { // Not OAI compatible, or embedding (embedding handled separately)
        choice_obj["text"] = content;
        if (generation_params.return_tokens) {
            json tokens_json = json::array();
            for(const auto& tok : tokens) {
                tokens_json.push_back(tok);
            }
            choice_obj["tokens"] = tokens_json;
        }
        if (stop != STOP_TYPE_NONE) {
            choice_obj["stop_reason"] = stop_type_to_str(stop);
        }
        if (truncated) {
            choice_obj["truncated"] = true;
        }
        if (!stopping_word.empty()) {
            choice_obj["stopping_word"] = stopping_word;
        }
        if (post_sampling_probs && !probs_output.empty()) {
            choice_obj["probs"] = completion_token_output::probs_vector_to_json(probs_output, true);
        }
    }

    choices_arr.push_back(choice_obj);

    if (oaicompat != OAICOMPAT_TYPE_NONE) {
        res_obj["id"] = oaicompat_cmpl_id;
        res_obj["object"] = (oaicompat == OAICOMPAT_TYPE_CHAT) ? (stream ? "chat.completion.chunk" : "chat.completion") :
                                                               (stream ? "text_completion.chunk" : "text_completion"); // TODO: check if text_completion.chunk is a thing
        res_obj["created"] = std::chrono::duration_cast<std::chrono::seconds>(std::chrono::system_clock::now().time_since_epoch()).count();
        res_obj["model"] = oaicompat_model;
        res_obj["choices"] = choices_arr;
        if (!stream || stop != STOP_TYPE_NONE || truncated) { // Add usage for final chunk or non-streamed response
            json usage_obj = json::object();
            usage_obj["prompt_tokens"] = n_prompt_tokens;
            usage_obj["completion_tokens"] = n_decoded;
            usage_obj["total_tokens"] = n_prompt_tokens + n_decoded;
            res_obj["usage"] = usage_obj;
        }
        if (stream && (stop == STOP_TYPE_NONE && !truncated)) {
             // For streaming chunks that are not the last one, OAI might not include usage or might have specific fields.
             // Current OAI behavior is to only send usage on the last chunk.
        } else if (stream) {
            // This is the last chunk of a stream
            // The finish_reason should already be in choices[0]
            // The usage object is added above.
        }

    } else { // Non-OAI format
        res_obj = choices_arr[0]; // The choice object itself is the response
        res_obj["id_slot"] = id_slot;
        res_obj["timings"] = timings.to_json();
        res_obj["generation_settings"] = generation_params.to_json();
        res_obj["prompt"] = prompt;
        res_obj["tokens_cached"] = n_tokens_cached;
        res_obj["tokens_decoded"] = n_decoded;
        res_obj["tokens_predicted"] = n_decoded; // Alias for consistency with older versions or other parts
        res_obj["has_new_line"] = has_new_line;

        // Include any extra fields requested
        for(const auto& field_name : response_fields) {
            if (field_name == "timings") res_obj["timings"] = timings.to_json();
            // Add other custom fields here if necessary
        }
    }

    return res_obj;
}
