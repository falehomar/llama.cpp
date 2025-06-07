package io.github.llama.impl.ffm;


import io.github.llama.impl.llamacpp.ffm.llama_h;
import org.junit.jupiter.api.Test;

public class ForeignLLMTest {
    static String MODEL_PATH = "/Users/e168693/.ollama/models/blobs/sha256-4ad960d180b16f56024f5b704697e5dd5b0837167c2e515ef0569abfc599743c";


    @Test
    public  void test() {


            llama_h.llama_backend_init();

            llama_h.llama_backend_free();




    }
}
