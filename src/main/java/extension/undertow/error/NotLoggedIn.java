package extension.undertow.error;

import io.undertow.server.HttpServerExchange;

import static extension.undertow.server.ResponseBuilder.respondError;
import static io.undertow.util.StatusCodes.FORBIDDEN;

/**
 * The {@code NotLoggedIn} class.
 */
public final class NotLoggedIn extends Exception implements HttpError {

    /**
     * The {@code errorCode} field.
     */
    private final int errorCode;
    /**
     * Creates a new instance.
     */
    public NotLoggedIn() {
        this(FORBIDDEN);
    }
    /**
     * Creates a new instance.
     *
     * @param errorCode the error code
     */
    public NotLoggedIn(final int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Performs the {@code processExchange} operation.
     *
     * @param exchange the exchange
     */
    @Override
    public void processExchange(final HttpServerExchange exchange) {
        respondError(exchange, FORBIDDEN, errorCode, "Not logged in");
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
