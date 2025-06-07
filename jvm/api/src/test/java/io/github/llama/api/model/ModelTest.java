package io.github.llama.api.model;

import io.github.llama.api.tokenization.Tokenizer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;

/**
 * Test class for the Model interface.
 */
public class ModelTest {

    /**
     * Test class that implements Model for testing.
     */
    private static class TestModel implements Model {
        private final ModelInfo modelInfo;
        private final Tokenizer tokenizer;
        private boolean closed = false;

        public TestModel(ModelInfo modelInfo, Tokenizer tokenizer) {
            this.modelInfo = modelInfo;
            this.tokenizer = tokenizer;
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
            closed = true;
        }

        public boolean isClosed() {
            return closed;
        }
    }

    @Test
    public void testGetModelInfo() {
        ModelInfo modelInfo = Mockito.mock(ModelInfo.class);
        Tokenizer tokenizer = Mockito.mock(Tokenizer.class);

        Model model = new TestModel(modelInfo, tokenizer);

        assertEquals(modelInfo, model.getModelInfo(), "Model info should match the one provided");
    }

    @Test
    public void testGetTokenizer() {
        ModelInfo modelInfo = Mockito.mock(ModelInfo.class);
        Tokenizer tokenizer = Mockito.mock(Tokenizer.class);

        Model model = new TestModel(modelInfo, tokenizer);

        assertEquals(tokenizer, model.getTokenizer(), "Tokenizer should match the one provided");
    }

    @Test
    public void testClose() {
        ModelInfo modelInfo = Mockito.mock(ModelInfo.class);
        Tokenizer tokenizer = Mockito.mock(Tokenizer.class);

        TestModel model = new TestModel(modelInfo, tokenizer);

        assertFalse(model.isClosed(), "Model should not be closed initially");

        model.close();

        assertTrue(model.isClosed(), "Model should be closed after calling close()");
    }
}
