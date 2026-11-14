package extension.undertow.error;

import io.undertow.server.HttpServerExchange;

import static extension.undertow.server.ResponseBuilder.respondError;
import static io.undertow.util.StatusCodes.FORBIDDEN;

/**
 * The {@code AccessDenied} class.
 */
public final class AccessDenied extends Exception implements HttpError {

    /**
     * The {@code errorCode} field.
     */
    private final int errorCode;
    /**
     * Creates a new instance.
     */
    public AccessDenied() {
        this(FORBIDDEN, "Forbidden");
    }
    /**
     * Creates a new instance.
     *
     * @param message the message
     */
    public AccessDenied(final String message) {
        this(FORBIDDEN, message);
    }
    /**
     * Creates a new instance.
     *
     * @param errorCode the error code
     * @param message the message
     */
    public AccessDenied(final int errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Performs the {@code processExchange} operation.
     *
     * @param exchange the exchange
     */
    @Override
    public void processExchange(final HttpServerExchange exchange) {
        respondError(exchange, FORBIDDEN, errorCode, getMessage());
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
