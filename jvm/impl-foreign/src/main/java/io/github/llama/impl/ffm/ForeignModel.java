package io.github.llama.impl.ffm;

import io.github.llama.api.model.Model;
import io.github.llama.api.model.ModelInfo;
import io.github.llama.api.tokenization.Tokenizer;
import io.github.llama.impl.llamacpp.ffm.llama_h;

import java.lang.foreign.MemorySegment;

/**
 * Implementation of the Model interface using the Foreign Function & Memory API.
 */
public class ForeignModel implements Model {

    // Native model pointer
    private final MemorySegment nativeModel;

    // Model info
    private final ModelInfo modelInfo;

    // Tokenizer
    private final Tokenizer tokenizer;

    /**
     * Creates a new ForeignModel.
     *
     * @param nativeModel Native model pointer
     */
    public ForeignModel(MemorySegment nativeModel) {
        this.nativeModel = nativeModel;
        this.modelInfo = new ForeignModelInfo(nativeModel);
        this.tokenizer = new ForeignTokenizer(nativeModel);
    }

    /**
     * Gets the native model pointer.
     *
     * @return Native model pointer
     */
    public MemorySegment getNativeModel() {
        return nativeModel;
    }

    @Override
    public ModelInfo getModelInfo() {
        return modelInfo;
    }

    @Override
    public Tokenizer getTokenizer() {
        return tokenizer;
    }

    @Override
    public void close() {
        // Free the model
        llama_h.llama_model_free(nativeModel);
    }
}
