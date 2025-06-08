#ifndef SERVER_HTTP_H
#define SERVER_HTTP_H

#include "server_types.h" // For server_context and other types
#include <httplib.h> // Assuming httplib.h is used, adjust if it's a different library

// Forward declaration
struct server_context;

namespace server_http {

    // Initialize and start the HTTP server
    void init_http_server(
        server_context & svr_ctx,
        httplib::Server & http_server
    );

    // Specific request handlers
    // These would be called by the routes set up in init_http_server

    // Handler for completion requests (/completion, /v1/completions, /v1/chat/completions)
    void handle_completion(
        const httplib::Request &req,
        httplib::Response &res,
        server_context &svr_ctx
    );

    // Handler for embedding requests (/embedding, /v1/embeddings)
    void handle_embedding(
        const httplib::Request &req,
        httplib::Response &res,
        server_context &svr_ctx
    );

    // Handler for infill requests (/infill) - if this is a distinct endpoint
    void handle_infill(
        const httplib::Request &req,
        httplib::Response &res,
        server_context &svr_ctx
    );

    // Handler for server events/streaming results (/results)
    void handle_server_events(
        const httplib::Request &req,
        httplib::Response &res,
        server_context &svr_ctx
    );

    // Handler for tokenization requests (/tokenize)
    void handle_tokenize(
        const httplib::Request &req,
        httplib::Response &res,
        server_context &svr_ctx
    );

    // Handler for detokenization requests (/detokenize)
    void handle_detokenize(
        const httplib::Request &req,
        httplib::Response &res,
        server_context &svr_ctx
    );

    // Handler for metrics endpoint (/metrics)
    void handle_metrics(
        const httplib::Request &req,
        httplib::Response &res,
        server_context &svr_ctx
    );

    // Handler for slots endpoint (/slots)
    void handle_slots_info(
        const httplib::Request &req,
        httplib::Response &res,
        server_context &svr_ctx
    );

    // Handler for slot save/restore/erase if these are HTTP endpoints
    void handle_slot_save(
        const httplib::Request &req,
        httplib::Response &res,
        server_context &svr_ctx
    );

    void handle_slot_restore(
        const httplib::Request &req,
        httplib::Response &res,
        server_context &svr_ctx
    );

    void handle_slot_erase(
        const httplib::Request &req,
        httplib::Response &res,
        server_context &svr_ctx
    );

    // Handler for LoRA adapter setting (/lora)
    void handle_set_lora(
        const httplib::Request &req,
        httplib::Response &res,
        server_context &svr_ctx
    );

    // Helper to send JSON response
    void send_json_response(
        httplib::Response &res,
        const json &body,
        int status_code = 200,
        const char *mime_type = MIMETYPE_JSON
    );

    // Helper to send error response
    void send_error_response(
        httplib::Response &res,
        const std::string &message,
        error_type type,
        int status_code = 500
    );

    // Helper to parse JSON from request body
    bool parse_json_request_body(
        const httplib::Request &req,
        json &parsed_json,
        httplib::Response &res // To send error if parsing fails
    );

} // namespace server_http

#endif // SERVER_HTTP_H
