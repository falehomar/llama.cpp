package io.github.llama.api.batch;

/**
 * Class representing the result of processing a batch.
 */
public class BatchResult {
    private final boolean success;
    private final String errorMessage;

    /**
     * Creates a successful batch result.
     */
    public BatchResult() {
        this.success = true;
        this.errorMessage = null;
    }

    /**
     * Creates a batch result with the specified success status and error message.
     *
     * @param success Whether the batch processing was successful
     * @param errorMessage Error message if the batch processing failed, or null if successful
     */
    public BatchResult(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    /**
     * Gets whether the batch processing was successful.
     *
     * @return Whether the batch processing was successful
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Gets the error message if the batch processing failed.
     *
     * @return Error message, or null if the batch processing was successful
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Creates a successful batch result.
     *
     * @return A successful batch result
     */
    public static BatchResult success() {
        return new BatchResult();
    }

    /**
     * Creates a failed batch result with the specified error message.
     *
     * @param errorMessage Error message
     * @return A failed batch result
     */
    public static BatchResult failure(String errorMessage) {
        return new BatchResult(false, errorMessage);
    }
}
