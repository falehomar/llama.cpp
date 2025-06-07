package io.github.llama.api.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Test class for the ModelInfo interface.
 */
public class ModelInfoTest {

    /**
     * Test class that implements ModelInfo for testing.
     */
    private static class TestModelInfo implements ModelInfo {
        @Override
        public long getParameterCount() {
            return 7000000000L; // 7B
        }

        @Override
        public int getContextSize() {
            return 2048;
        }

        @Override
        public int getEmbeddingSize() {
            return 4096;
        }

        @Override
        public int getLayerCount() {
            return 32;
        }

        @Override
        public int getHeadCount() {
            return 32;
        }

        @Override
        public int getKvHeadCount() {
            return 32;
        }

        @Override
        public float getRopeFreqScaleTrain() {
            return 1.0f;
        }

        @Override
        public int getRopeType() {
            return 0;
        }

        @Override
        public String getMetadata(String key) {
            if ("name".equals(key)) {
                return "llama-7b";
            }
            return null;
        }

        @Override
        public Set<String> getMetadataKeys() {
            Set<String> keys = new HashSet<>();
            keys.add("name");
            return keys;
        }

        @Override
        public String getDescription() {
            return "Llama 7B model";
        }

        @Override
        public long getSize() {
            return 7000000000L; // 7GB
        }

        @Override
        public String getChatTemplate() {
            return "<|im_start|>user\n{prompt}<|im_end|>\n<|im_start|>assistant\n";
        }

        @Override
        public boolean hasEncoder() {
            return false;
        }

        @Override
        public boolean hasDecoder() {
            return true;
        }

        @Override
        public int getDecoderStartToken() {
            return 1;
        }

        @Override
        public boolean isRecurrent() {
            return false;
        }
    }

    @Test
    public void testGetParameterCount() {
        ModelInfo modelInfo = new TestModelInfo();
        assertEquals(7000000000L, modelInfo.getParameterCount(), "Parameter count should be 7B");
    }

    @Test
    public void testGetContextSize() {
        ModelInfo modelInfo = new TestModelInfo();
        assertEquals(2048, modelInfo.getContextSize(), "Context size should be 2048");
    }

    @Test
    public void testGetEmbeddingSize() {
        ModelInfo modelInfo = new TestModelInfo();
        assertEquals(4096, modelInfo.getEmbeddingSize(), "Embedding size should be 4096");
    }

    @Test
    public void testGetLayerCount() {
        ModelInfo modelInfo = new TestModelInfo();
        assertEquals(32, modelInfo.getLayerCount(), "Layer count should be 32");
    }

    @Test
    public void testGetHeadCount() {
        ModelInfo modelInfo = new TestModelInfo();
        assertEquals(32, modelInfo.getHeadCount(), "Head count should be 32");
    }

    @Test
    public void testGetKvHeadCount() {
        ModelInfo modelInfo = new TestModelInfo();
        assertEquals(32, modelInfo.getKvHeadCount(), "KV head count should be 32");
    }

    @Test
    public void testGetRopeFreqScaleTrain() {
        ModelInfo modelInfo = new TestModelInfo();
        assertEquals(1.0f, modelInfo.getRopeFreqScaleTrain(), "RoPE frequency scaling factor should be 1.0");
    }

    @Test
    public void testGetRopeType() {
        ModelInfo modelInfo = new TestModelInfo();
        assertEquals(0, modelInfo.getRopeType(), "RoPE type should be 0");
    }

    @Test
    public void testGetMetadata() {
        ModelInfo modelInfo = new TestModelInfo();
        assertEquals("llama-7b", modelInfo.getMetadata("name"), "Metadata for 'name' should be 'llama-7b'");
        assertNull(modelInfo.getMetadata("unknown"), "Metadata for unknown key should be null");
    }

    @Test
    public void testGetMetadataKeys() {
        ModelInfo modelInfo = new TestModelInfo();
        Set<String> keys = modelInfo.getMetadataKeys();
        assertEquals(1, keys.size(), "There should be 1 metadata key");
        assertTrue(keys.contains("name"), "Metadata keys should contain 'name'");
    }

    @Test
    public void testGetDescription() {
        ModelInfo modelInfo = new TestModelInfo();
        assertEquals("Llama 7B model", modelInfo.getDescription(), "Description should match");
    }

    @Test
    public void testGetSize() {
        ModelInfo modelInfo = new TestModelInfo();
        assertEquals(7000000000L, modelInfo.getSize(), "Size should be 7GB");
    }

    @Test
    public void testGetChatTemplate() {
        ModelInfo modelInfo = new TestModelInfo();
        assertEquals("<|im_start|>user\n{prompt}<|im_end|>\n<|im_start|>assistant\n",
                     modelInfo.getChatTemplate(), "Chat template should match");
    }

    @Test
    public void testHasEncoder() {
        ModelInfo modelInfo = new TestModelInfo();
        assertFalse(modelInfo.hasEncoder(), "Model should not have an encoder");
    }

    @Test
    public void testHasDecoder() {
        ModelInfo modelInfo = new TestModelInfo();
        assertTrue(modelInfo.hasDecoder(), "Model should have a decoder");
    }

    @Test
    public void testGetDecoderStartToken() {
        ModelInfo modelInfo = new TestModelInfo();
        assertEquals(1, modelInfo.getDecoderStartToken(), "Decoder start token should be 1");
    }

    @Test
    public void testIsRecurrent() {
        ModelInfo modelInfo = new TestModelInfo();
        assertFalse(modelInfo.isRecurrent(), "Model should not be recurrent");
    }
}
