package extension.undertow.error;

import io.undertow.server.HttpServerExchange;

import java.io.IOException;

import static extension.undertow.server.ResponseBuilder.respondError;
import static io.undertow.util.StatusCodes.BAD_GATEWAY;

/**
 * The {@code RemoteServiceFailure} class.
 */
public class RemoteServiceFailure extends IOException implements HttpError {
    /**
     * The {@code errorCode} field.
     */
    private final int errorCode;
    /**
     * Creates a new instance.
     *
     * @param message the message
     */
    public RemoteServiceFailure(final String message) {
        this(BAD_GATEWAY, message);
    }
    /**
     * Creates a new instance.
     *
     * @param errorCode the error code
     * @param message the message
     */
    public RemoteServiceFailure(final int errorCode, final String message) {
        this(errorCode, message, null);
    }
    /**
     * Creates a new instance.
     *
     * @param errorCode the error code
     * @param message the message
     * @param cause the cause
     */
    public RemoteServiceFailure(final int errorCode, final String message, final Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Performs the {@code processExchange} operation.
     *
     * @param exchange the exchange
     */
    @Override
    public void processExchange(final HttpServerExchange exchange) {
        respondError(exchange, BAD_GATEWAY, errorCode, "External app.error");
    }

    /**
     * Returns whether the server error condition holds.
     *
     * @return whether the condition holds
     */
    public boolean isServerError() {
        return true;
    }
}
