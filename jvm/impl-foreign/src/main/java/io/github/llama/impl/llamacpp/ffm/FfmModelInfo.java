package io.github.llama.impl.llamacpp.ffm;

import io.github.llama.api.model.ModelInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of {@link ModelInfo} using Java's Foreign Function & Memory API.
 * This class provides information about a loaded llama.cpp model.
 */
public class FfmModelInfo implements ModelInfo {

    private static final Logger logger = LoggerFactory.getLogger(FfmModelInfo.class);

    private final long parameterCount;
    private final int contextSize;
    private final int embeddingSize;
    private final int layerCount;
    private final int headCount;
    private final int kvHeadCount;
    private final float ropeFreqScaleTrain;
    private final int ropeType;
    private final Map<String, String> metadata;
    private final String description;
    private final long size;
    private final String chatTemplate;
    private final boolean hasEncoder;
    private final boolean hasDecoder;
    private final int decoderStartToken;
    private final boolean recurrent;

    /**
     * Creates a new instance of the FfmModelInfo.
     *
     * @param parameterCount Number of parameters in the model
     * @param contextSize Context size used during training
     * @param embeddingSize Embedding size
     * @param layerCount Number of layers
     * @param headCount Number of attention heads
     * @param kvHeadCount Number of key-value heads
     * @param ropeFreqScaleTrain RoPE frequency scaling factor
     * @param ropeType RoPE type
     * @param metadata Metadata map
     * @param description Model description
     * @param size Total size of all tensors in the model
     * @param chatTemplate Default chat template
     * @param hasEncoder Whether the model contains an encoder
     * @param hasDecoder Whether the model contains a decoder
     * @param decoderStartToken Decoder start token
     * @param recurrent Whether the model is recurrent
     */
    public FfmModelInfo(
            long parameterCount,
            int contextSize,
            int embeddingSize,
            int layerCount,
            int headCount,
            int kvHeadCount,
            float ropeFreqScaleTrain,
            int ropeType,
            Map<String, String> metadata,
            String description,
            long size,
            String chatTemplate,
            boolean hasEncoder,
            boolean hasDecoder,
            int decoderStartToken,
            boolean recurrent
    ) {
        this.parameterCount = parameterCount;
        this.contextSize = contextSize;
        this.embeddingSize = embeddingSize;
        this.layerCount = layerCount;
        this.headCount = headCount;
        this.kvHeadCount = kvHeadCount;
        this.ropeFreqScaleTrain = ropeFreqScaleTrain;
        this.ropeType = ropeType;
        this.metadata = new HashMap<>(metadata);
        this.description = description;
        this.size = size;
        this.chatTemplate = chatTemplate;
        this.hasEncoder = hasEncoder;
        this.hasDecoder = hasDecoder;
        this.decoderStartToken = decoderStartToken;
        this.recurrent = recurrent;

        logger.debug("Created FfmModelInfo: {}", description);
    }

    @Override
    public long getParameterCount() {
        return parameterCount;
    }

    @Override
    public int getContextSize() {
        return contextSize;
    }

    @Override
    public int getEmbeddingSize() {
        return embeddingSize;
    }

    @Override
    public int getLayerCount() {
        return layerCount;
    }

    @Override
    public int getHeadCount() {
        return headCount;
    }

    @Override
    public int getKvHeadCount() {
        return kvHeadCount;
    }

    @Override
    public float getRopeFreqScaleTrain() {
        return ropeFreqScaleTrain;
    }

    @Override
    public int getRopeType() {
        return ropeType;
    }

    @Override
    public String getMetadata(String key) {
        return metadata.get(key);
    }

    @Override
    public Set<String> getMetadataKeys() {
        return Collections.unmodifiableSet(metadata.keySet());
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public String getChatTemplate() {
        return chatTemplate;
    }

    @Override
    public boolean hasEncoder() {
        return hasEncoder;
    }

    @Override
    public boolean hasDecoder() {
        return hasDecoder;
    }

    @Override
    public int getDecoderStartToken() {
        return decoderStartToken;
    }

    @Override
    public boolean isRecurrent() {
        return recurrent;
    }

    /**
     * Builder for FfmModelInfo.
     */
    public static class Builder {
        private long parameterCount = 0;
        private int contextSize = 0;
        private int embeddingSize = 0;
        private int layerCount = 0;
        private int headCount = 0;
        private int kvHeadCount = 0;
        private float ropeFreqScaleTrain = 0.0f;
        private int ropeType = 0;
        private final Map<String, String> metadata = new HashMap<>();
        private String description = "";
        private long size = 0;
        private String chatTemplate = null;
        private boolean hasEncoder = false;
        private boolean hasDecoder = true;
        private int decoderStartToken = 0;
        private boolean recurrent = false;

        public Builder parameterCount(long parameterCount) {
            this.parameterCount = parameterCount;
            return this;
        }

        public Builder contextSize(int contextSize) {
            this.contextSize = contextSize;
            return this;
        }

        public Builder embeddingSize(int embeddingSize) {
            this.embeddingSize = embeddingSize;
            return this;
        }

        public Builder layerCount(int layerCount) {
            this.layerCount = layerCount;
            return this;
        }

        public Builder headCount(int headCount) {
            this.headCount = headCount;
            return this;
        }

        public Builder kvHeadCount(int kvHeadCount) {
            this.kvHeadCount = kvHeadCount;
            return this;
        }

        public Builder ropeFreqScaleTrain(float ropeFreqScaleTrain) {
            this.ropeFreqScaleTrain = ropeFreqScaleTrain;
            return this;
        }

        public Builder ropeType(int ropeType) {
            this.ropeType = ropeType;
            return this;
        }

        public Builder addMetadata(String key, String value) {
            this.metadata.put(key, value);
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder size(long size) {
            this.size = size;
            return this;
        }

        public Builder chatTemplate(String chatTemplate) {
            this.chatTemplate = chatTemplate;
            return this;
        }

        public Builder hasEncoder(boolean hasEncoder) {
            this.hasEncoder = hasEncoder;
            return this;
        }

        public Builder hasDecoder(boolean hasDecoder) {
            this.hasDecoder = hasDecoder;
            return this;
        }

        public Builder decoderStartToken(int decoderStartToken) {
            this.decoderStartToken = decoderStartToken;
            return this;
        }

        public Builder recurrent(boolean recurrent) {
            this.recurrent = recurrent;
            return this;
        }

        public FfmModelInfo build() {
            return new FfmModelInfo(
                    parameterCount,
                    contextSize,
                    embeddingSize,
                    layerCount,
                    headCount,
                    kvHeadCount,
                    ropeFreqScaleTrain,
                    ropeType,
                    metadata,
                    description,
                    size,
                    chatTemplate,
                    hasEncoder,
                    hasDecoder,
                    decoderStartToken,
                    recurrent
            );
        }
    }
}
