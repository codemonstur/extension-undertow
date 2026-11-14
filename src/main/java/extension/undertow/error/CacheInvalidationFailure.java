package extension.undertow.error;

/**
 * The {@code CacheInvalidationFailure} class.
 */
public class CacheInvalidationFailure extends Exception {
    /**
     * Creates a new instance.
     *
     * @param cause the cause
     */
    public CacheInvalidationFailure(final Exception cause) {
        super(cause);
    }
}
