package extension.undertow.server;

/**
 * The {@code HandlerLogger} interface.
 */
public interface HandlerLogger {
    /**
     * Performs the {@code error} operation.
     *
     * @param message the message
     */
    void error(Object message);
    /**
     * Performs the {@code info} operation.
     *
     * @param message the message
     */
    void info(String message);
}
