package extension.undertow.error;

import io.undertow.server.HttpServerExchange;

import java.io.IOException;

import static io.undertow.util.StatusCodes.NOT_FOUND;

/**
 * The {@code NotFound} class.
 */
public final class NotFound extends IOException implements HttpError {
    /**
     * Creates a new instance.
     */
    public NotFound() {}
    /**
     * Creates a new instance.
     *
     * @param message the message
     */
    public NotFound(final String message) {
        super(message);
    }

    /**
     * Performs the {@code processExchange} operation.
     *
     * @param exchange the exchange
     */
    @Override
    public void processExchange(final HttpServerExchange exchange) {
        exchange.setStatusCode(NOT_FOUND);
    }

    /**
     * Returns whether the server error condition holds.
     *
     * @return whether the condition holds
     */
    public boolean isServerError() {
        return false;
    }
}
