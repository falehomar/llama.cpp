// Java Native Interface (JNI) wrapper for llama.cpp library
// This file contains native methods that interface with the llama.cpp C API

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Include llama.cpp headers
#include "llama.h"

// Helper function to throw Java exception
void throwJavaException(JNIEnv *env, const char *className, const char *message) {
    jclass exClass = (*env)->FindClass(env, className);
    if (exClass != NULL) {
        (*env)->ThrowNew(env, exClass, message);
    }
    (*env)->DeleteLocalRef(env, exClass);
}

// Convert Java ModelParams to native llama_model_params
struct llama_model_params javaToNativeModelParams(JNIEnv *env, jobject jModelParams) {
    struct llama_model_params params = llama_model_default_params();

    jclass modelParamsClass = (*env)->GetObjectClass(env, jModelParams);

    // Get field IDs
    jfieldID useMemMappingField = (*env)->GetFieldID(env, modelParamsClass, "useMemoryMapping", "Z");
    jfieldID useMemLockingField = (*env)->GetFieldID(env, modelParamsClass, "useMemoryLocking", "Z");
    jfieldID gpuLayerCountField = (*env)->GetFieldID(env, modelParamsClass, "gpuLayerCount", "I");
    jfieldID vocabOnlyField = (*env)->GetFieldID(env, modelParamsClass, "vocabOnly", "Z");

    // Extract values from Java object
    params.use_mmap = (*env)->GetBooleanField(env, jModelParams, useMemMappingField);
    params.use_mlock = (*env)->GetBooleanField(env, jModelParams, useMemLockingField);
    params.n_gpu_layers = (*env)->GetIntField(env, jModelParams, gpuLayerCountField);
    params.vocab_only = (*env)->GetBooleanField(env, jModelParams, vocabOnlyField);

    // TODO: Handle metadata overrides if needed

    return params;
}

// Convert native ModelInfo to Java ModelInfo
jobject nativeToJavaModelInfo(JNIEnv *env, const struct llama_model *model) {
    jclass modelInfoClass = (*env)->FindClass(env, "io/github/llama/api/model/ModelInfo");
    jmethodID constructor = (*env)->GetMethodID(env, modelInfoClass, "<init>", "()V");
    jobject modelInfo = (*env)->NewObject(env, modelInfoClass, constructor);

    // Get field IDs
    jfieldID nameField = (*env)->GetFieldID(env, modelInfoClass, "name", "Ljava/lang/String;");
    jfieldID descriptionField = (*env)->GetFieldID(env, modelInfoClass, "description", "Ljava/lang/String;");
    jfieldID vocabSizeField = (*env)->GetFieldID(env, modelInfoClass, "vocabSize", "I");
    jfieldID contextSizeField = (*env)->GetFieldID(env, modelInfoClass, "contextSize", "I");

    // Get values from native model
    const char *name = llama_model_name(model);
    int vocabSize = llama_n_vocab(model);
    int contextSize = llama_n_ctx_train(model);

    // Set Java fields
    (*env)->SetObjectField(env, modelInfo, nameField, (*env)->NewStringUTF(env, name ? name : "Unknown"));
    (*env)->SetObjectField(env, modelInfo, descriptionField, (*env)->NewStringUTF(env, ""));
    (*env)->SetIntField(env, modelInfo, vocabSizeField, vocabSize);
    (*env)->SetIntField(env, modelInfo, contextSizeField, contextSize);

    return modelInfo;
}

// Convert Java ContextParams to native llama_context_params
struct llama_context_params javaToNativeContextParams(JNIEnv *env, jobject jContextParams) {
    struct llama_context_params params = llama_context_default_params();

    jclass contextParamsClass = (*env)->GetObjectClass(env, jContextParams);

    // Get field IDs
    jfieldID contextSizeField = (*env)->GetFieldID(env, contextParamsClass, "contextSize", "I");
    jfieldID batchSizeField = (*env)->GetFieldID(env, contextParamsClass, "batchSize", "I");
    jfieldID threadCountField = (*env)->GetFieldID(env, contextParamsClass, "threadCount", "I");

    // Extract values
    int contextSize = (*env)->GetIntField(env, jContextParams, contextSizeField);
    int batchSize = (*env)->GetIntField(env, jContextParams, batchSizeField);
    int threadCount = (*env)->GetIntField(env, jContextParams, threadCountField);

    // Set native params
    if (contextSize > 0) params.n_ctx = contextSize;
    if (batchSize > 0) params.n_batch = batchSize;
    params.n_threads = threadCount > 0 ? threadCount : params.n_threads;

    return params;
}

// Helper function to create a new Java batch object
jobject createJavaBatch(JNIEnv *env, struct llama_batch *batch, int maxTokens) {
    jclass batchClass = (*env)->FindClass(env, "io/github/llama/api/batch/BatchImpl");
    jmethodID constructor = (*env)->GetMethodID(env, batchClass, "<init>", "(I)V");

    jobject javaBatch = (*env)->NewObject(env, batchClass, constructor, maxTokens);

    // Store the native batch pointer in the Java object
    jfieldID nativePtrField = (*env)->GetFieldID(env, batchClass, "nativePtr", "J");
    (*env)->SetLongField(env, javaBatch, nativePtrField, (jlong)batch);

    return javaBatch;
}

// JNI method implementations

/*
 * Class:     io_github_llama_impl_jni_LlamaJniBackend
 * Method:    llama_backend_init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_io_github_llama_impl_jni_LlamaJniBackend_llama_1backend_1init
  (JNIEnv *env, jclass cls) {
    llama_backend_init();
}

/*
 * Class:     io_github_llama_impl_jni_LlamaJniBackend
 * Method:    llama_backend_free
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_io_github_llama_impl_jni_LlamaJniBackend_llama_1backend_1free
  (JNIEnv *env, jclass cls) {
    llama_backend_free();
}

/*
 * Class:     io_github_llama_impl_jni_LlamaJniBackend
 * Method:    llama_model_load_from_file
 * Signature: (Ljava/lang/String;Lio/github/llama/api/model/ModelParams;)J
 */
JNIEXPORT jlong JNICALL Java_io_github_llama_impl_jni_LlamaJniBackend_llama_1model_1load_1from_1file
  (JNIEnv *env, jclass cls, jstring jModelPath, jobject jModelParams) {
    const char *modelPath = (*env)->GetStringUTFChars(env, jModelPath, NULL);
    if (modelPath == NULL) {
        throwJavaException(env, "java/lang/IllegalArgumentException", "Invalid model path");
        return 0;
    }

    struct llama_model_params params = javaToNativeModelParams(env, jModelParams);
    struct llama_model *model = llama_model_load_from_file(modelPath, params);

    (*env)->ReleaseStringUTFChars(env, jModelPath, modelPath);

    if (model == NULL) {
        throwJavaException(env, "java/io/IOException", "Failed to load model");
        return 0;
    }

    return (jlong)model;
}

/*
 * Class:     io_github_llama_impl_jni_LlamaJniBackend
 * Method:    llama_model_free
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_io_github_llama_impl_jni_LlamaJniBackend_llama_1model_1free
  (JNIEnv *env, jclass cls, jlong jModel) {
    struct llama_model *model = (struct llama_model *)jModel;
    if (model != NULL) {
        llama_model_free(model);
    }
}

/*
 * Class:     io_github_llama_impl_jni_LlamaJniBackend
 * Method:    llama_get_model_info
 * Signature: (J)Lio/github/llama/api/model/ModelInfo;
 */
JNIEXPORT jobject JNICALL Java_io_github_llama_impl_jni_LlamaJniBackend_llama_1get_1model_1info
  (JNIEnv *env, jclass cls, jlong jModel) {
    struct llama_model *model = (struct llama_model *)jModel;
    if (model == NULL) {
        throwJavaException(env, "java/lang/IllegalArgumentException", "Model is null");
        return NULL;
    }

    return nativeToJavaModelInfo(env, model);
}

/*
 * Class:     io_github_llama_impl_jni_LlamaJniBackend
 * Method:    llama_context_create
 * Signature: (JLio/github/llama/api/context/ContextParams;)J
 */
JNIEXPORT jlong JNICALL Java_io_github_llama_impl_jni_LlamaJniBackend_llama_1context_1create
  (JNIEnv *env, jclass cls, jlong jModel, jobject jContextParams) {
    struct llama_model *model = (struct llama_model *)jModel;
    if (model == NULL) {
        throwJavaException(env, "java/lang/IllegalArgumentException", "Model is null");
        return 0;
    }

    struct llama_context_params params = javaToNativeContextParams(env, jContextParams);
    struct llama_context *ctx = llama_context_create(model, params);

    if (ctx == NULL) {
        throwJavaException(env, "java/lang/RuntimeException", "Failed to create context");
        return 0;
    }

    return (jlong)ctx;
}

/*
 * Class:     io_github_llama_impl_jni_LlamaJniBackend
 * Method:    llama_context_free
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_io_github_llama_impl_jni_LlamaJniBackend_llama_1context_1free
  (JNIEnv *env, jclass cls, jlong jContext) {
    struct llama_context *ctx = (struct llama_context *)jContext;
    if (ctx != NULL) {
        llama_context_free(ctx);
    }
}

/*
 * Class:     io_github_llama_impl_jni_LlamaJniBackend
 * Method:    llama_batch_create
 * Signature: (I)J
 */
JNIEXPORT jlong JNICALL Java_io_github_llama_impl_jni_LlamaJniBackend_llama_1batch_1create
  (JNIEnv *env, jclass cls, jint jMaxTokens) {
    struct llama_batch *batch = (struct llama_batch *)malloc(sizeof(struct llama_batch));
    if (batch == NULL) {
        throwJavaException(env, "java/lang/OutOfMemoryError", "Failed to allocate memory for batch");
        return 0;
    }

    *batch = llama_batch_init(jMaxTokens, 0, 1);

    return (jlong)batch;
}

/*
 * Class:     io_github_llama_impl_jni_LlamaJniBackend
 * Method:    llama_batch_add
 * Signature: (JII)Z
 */
JNIEXPORT jboolean JNICALL Java_io_github_llama_impl_jni_LlamaJniBackend_llama_1batch_1add
  (JNIEnv *env, jclass cls, jlong jBatch, jint jToken, jint jPosition) {
    struct llama_batch *batch = (struct llama_batch *)jBatch;
    if (batch == NULL) {
        throwJavaException(env, "java/lang/IllegalArgumentException", "Batch is null");
        return JNI_FALSE;
    }

    if (batch->n_tokens >= batch->n_tokens_capacity) {
        return JNI_FALSE; // Batch is full
    }

    llama_batch_add(batch, jToken, jPosition, (llama_seq_id []){0}, false);

    return JNI_TRUE;
}

/*
 * Class:     io_github_llama_impl_jni_LlamaJniBackend
 * Method:    llama_batch_free
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_io_github_llama_impl_jni_LlamaJniBackend_llama_1batch_1free
  (JNIEnv *env, jclass cls, jlong jBatch) {
    struct llama_batch *batch = (struct llama_batch *)jBatch;
    if (batch != NULL) {
        // Free any allocated memory within the batch
        if (batch->token != NULL) {
            free(batch->token);
        }
        if (batch->pos != NULL) {
            free(batch->pos);
        }
        if (batch->n_seq_id != NULL) {
            free(batch->n_seq_id);
        }
        if (batch->seq_id != NULL && batch->seq_id[0] != NULL) {
            free(batch->seq_id[0]);
        }
        if (batch->seq_id != NULL) {
            free(batch->seq_id);
        }
        if (batch->logits != NULL) {
            free(batch->logits);
        }

        // Free the batch itself
        free(batch);
    }
}

/*
 * Class:     io_github_llama_impl_jni_LlamaJniBackend
 * Method:    llama_decode
 * Signature: (JJ)I
 */
JNIEXPORT jint JNICALL Java_io_github_llama_impl_jni_LlamaJniBackend_llama_1decode
  (JNIEnv *env, jclass cls, jlong jContext, jlong jBatch) {
    struct llama_context *ctx = (struct llama_context *)jContext;
    struct llama_batch *batch = (struct llama_batch *)jBatch;

    if (ctx == NULL || batch == NULL) {
        throwJavaException(env, "java/lang/IllegalArgumentException", "Context or batch is null");
        return -1;
    }

    return llama_decode(ctx, *batch);
}

/*
 * Class:     io_github_llama_impl_jni_LlamaJniBackend
 * Method:    llama_get_logits
 * Signature: (J)[F
 */
JNIEXPORT jfloatArray JNICALL Java_io_github_llama_impl_jni_LlamaJniBackend_llama_1get_1logits
  (JNIEnv *env, jclass cls, jlong jContext) {
    struct llama_context *ctx = (struct llama_context *)jContext;
    if (ctx == NULL) {
        throwJavaException(env, "java/lang/IllegalArgumentException", "Context is null");
        return NULL;
    }

    int vocabSize = llama_n_vocab(llama_context_model(ctx));
    const float *logits = llama_get_logits(ctx);

    jfloatArray result = (*env)->NewFloatArray(env, vocabSize);
    if (result == NULL) {
        throwJavaException(env, "java/lang/OutOfMemoryError", "Failed to allocate memory for logits");
        return NULL;
    }

    (*env)->SetFloatArrayRegion(env, result, 0, vocabSize, logits);

    return result;
}

/*
 * Class:     io_github_llama_impl_jni_LlamaJniBackend
 * Method:    llama_token_to_piece
 * Signature: (JI[B)I
 */
JNIEXPORT jint JNICALL Java_io_github_llama_impl_jni_LlamaJniBackend_llama_1token_1to_1piece
  (JNIEnv *env, jclass cls, jlong jModel, jint jToken, jbyteArray jBuffer) {
    struct llama_model *model = (struct llama_model *)jModel;
    if (model == NULL) {
        throwJavaException(env, "java/lang/IllegalArgumentException", "Model is null");
        return -1;
    }

    jsize bufferSize = (*env)->GetArrayLength(env, jBuffer);
    jbyte *buffer = (*env)->GetByteArrayElements(env, jBuffer, NULL);

    int result = llama_token_to_piece(model, jToken, (char *)buffer, bufferSize);

    (*env)->ReleaseByteArrayElements(env, jBuffer, buffer, 0);

    return result;
}

/*
 * Class:     io_github_llama_impl_jni_LlamaJniBackend
 * Method:    llama_tokenize
 * Signature: (JLjava/lang/String;Z)[I
 */
JNIEXPORT jintArray JNICALL Java_io_github_llama_impl_jni_LlamaJniBackend_llama_1tokenize
  (JNIEnv *env, jclass cls, jlong jModel, jstring jText, jboolean jAddBos) {
    struct llama_model *model = (struct llama_model *)jModel;
    if (model == NULL) {
        throwJavaException(env, "java/lang/IllegalArgumentException", "Model is null");
        return NULL;
    }

    const char *text = (*env)->GetStringUTFChars(env, jText, NULL);
    if (text == NULL) {
        return NULL;
    }

    // First pass: determine the number of tokens
    int numTokens = llama_tokenize(model, text, strlen(text), NULL, 0, jAddBos, false);
    if (numTokens < 0) {
        (*env)->ReleaseStringUTFChars(env, jText, text);
        throwJavaException(env, "java/lang/RuntimeException", "Tokenization failed");
        return NULL;
    }

    // Allocate array for tokens
    llama_token *tokens = malloc(numTokens * sizeof(llama_token));
    if (tokens == NULL) {
        (*env)->ReleaseStringUTFChars(env, jText, text);
        throwJavaException(env, "java/lang/OutOfMemoryError", "Failed to allocate memory for tokens");
        return NULL;
    }

    // Second pass: actually tokenize
    int actualTokens = llama_tokenize(model, text, strlen(text), tokens, numTokens, jAddBos, false);

    (*env)->ReleaseStringUTFChars(env, jText, text);

    if (actualTokens < 0) {
        free(tokens);
        throwJavaException(env, "java/lang/RuntimeException", "Tokenization failed");
        return NULL;
    }

    // Convert to Java array
    jintArray result = (*env)->NewIntArray(env, actualTokens);
    if (result == NULL) {
        free(tokens);
        throwJavaException(env, "java/lang/OutOfMemoryError", "Failed to allocate Java array");
        return NULL;
    }

    (*env)->SetIntArrayRegion(env, result, 0, actualTokens, (jint *)tokens);
    free(tokens);

    return result;
}

/*
 * Class:     io_github_llama_impl_jni_LlamaJniBackend
 * Method:    llama_get_model_default_params
 * Signature: ()Lio/github/llama/api/model/ModelParams;
 */
JNIEXPORT jobject JNICALL Java_io_github_llama_impl_jni_LlamaJniBackend_llama_1get_1model_1default_1params
  (JNIEnv *env, jclass cls) {
    struct llama_model_params params = llama_model_default_params();

    // Create a new ModelParams Java object
    jclass modelParamsClass = (*env)->FindClass(env, "io/github/llama/api/model/ModelParams");
    jmethodID constructor = (*env)->GetMethodID(env, modelParamsClass, "<init>", "()V");
    jobject jModelParams = (*env)->NewObject(env, modelParamsClass, constructor);

    // Get field IDs
    jfieldID useMemMappingField = (*env)->GetFieldID(env, modelParamsClass, "useMemoryMapping", "Z");
    jfieldID useMemLockingField = (*env)->GetFieldID(env, modelParamsClass, "useMemoryLocking", "Z");
    jfieldID gpuLayerCountField = (*env)->GetFieldID(env, modelParamsClass, "gpuLayerCount", "I");
    jfieldID vocabOnlyField = (*env)->GetFieldID(env, modelParamsClass, "vocabOnly", "Z");

    // Set Java fields from native params
    (*env)->SetBooleanField(env, jModelParams, useMemMappingField, params.use_mmap);
    (*env)->SetBooleanField(env, jModelParams, useMemLockingField, params.use_mlock);
    (*env)->SetIntField(env, jModelParams, gpuLayerCountField, params.n_gpu_layers);
    (*env)->SetBooleanField(env, jModelParams, vocabOnlyField, params.vocab_only);

    return jModelParams;
}
