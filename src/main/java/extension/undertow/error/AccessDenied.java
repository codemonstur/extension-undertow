package extension.undertow.error;

import io.undertow.server.HttpServerExchange;

import static extension.undertow.server.ResponseBuilder.respondError;
import static io.undertow.util.StatusCodes.FORBIDDEN;

public final class AccessDenied extends Exception implements HttpError {

    private final int errorCode;
    public AccessDenied() {
        this(FORBIDDEN, "Forbidden");
    }
    public AccessDenied(final String message) {
        this(FORBIDDEN, message);
    }
    public AccessDenied(final int errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
    }

    @Override
    public void processExchange(final HttpServerExchange exchange) {
        respondError(exchange, FORBIDDEN, errorCode, getMessage());
    }

    public boolean isServerError() {
        return false;
    }
}
