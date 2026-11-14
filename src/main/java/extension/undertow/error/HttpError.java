package extension.undertow.error;

import io.undertow.server.HttpServerExchange;

/**
 * The {@code HttpError} interface.
 */
public interface HttpError {
    /**
     * Performs the {@code processExchange} operation.
     *
     * @param exchange the exchange
     */
    void processExchange(HttpServerExchange exchange);
    /**
     * Returns whether the server error condition holds.
     *
     * @return whether the condition holds
     */
    boolean isServerError();

    /**
     * The {@code ServerHttpError} interface.
     */
    public interface ServerHttpError extends HttpError {
        /**
         * Returns whether the server error condition holds.
         *
         * @return whether the condition holds
         */
        default boolean isServerError() {
            return true;
        }
    }
    /**
     * The {@code ClientHttpError} interface.
     */
    public interface ClientHttpError extends HttpError {
        /**
         * Returns whether the server error condition holds.
         *
         * @return whether the condition holds
         */
        default boolean isServerError() {
            return false;
        }
    }
}
