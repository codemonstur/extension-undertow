package extension.undertow.error;

public class CacheInvalidationFailure extends Exception {
    public CacheInvalidationFailure(final Exception cause) {
        super(cause);
    }
}
