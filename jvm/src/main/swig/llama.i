%module(package="com.llama", javapackage="com.llama") llama

%{
#include "llama.h"
%}

// Include required SWIG Java support
%include "typemaps.i"
%include "cpointer.i"
%include "cstring.i"
%include "stdint.i"
%include "arrays_java.i"

%feature("director") llama_progress_callback;

// Export all headers that define your API
%include "ggml.h"
%include "ggml-cpu.h"
%include "ggml-backend.h"
%include "ggml-opt.h"

// Typedefs for Java
%typemap(jstype) int32_t "int"
%typemap(jstype) int64_t "long"
%typemap(jstype) float "float"
%typemap(jstype) double "double"
%typemap(jstype) bool "boolean"

// Forward declarations for pointer types
%typemap(jstype) struct llama_model * "long"
%typemap(jstype) struct llama_context * "long"
%typemap(jstype) struct llama_sampler * "long"
%typemap(jstype) struct llama_kv_cache * "long"
%typemap(jstype) struct llama_vocab * "long"

// Typedefs
%typemap(jstype) llama_pos "int"
%typemap(jstype) llama_token "int"
%typemap(jstype) llama_seq_id "int"

%typedef int32_t llama_pos;
%typedef int32_t llama_token;
%typedef int32_t llama_seq_id;

// Enums
%include "llama.h"

// Structs and Callbacks
%callback bool (*llama_progress_callback)(float progress, void * user_data);

// You may want to %ignore some low-level functions or members not needed in Java

// Optionally, wrap extra helper functions for Java usability here
