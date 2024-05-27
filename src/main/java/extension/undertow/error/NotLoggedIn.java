package extension.undertow.error;

import io.undertow.server.HttpServerExchange;

import static extension.undertow.server.ResponseBuilder.respondError;
import static io.undertow.util.StatusCodes.FORBIDDEN;

public final class NotLoggedIn extends Exception implements HttpError {

    private final int errorCode;
    public NotLoggedIn() {
        this(FORBIDDEN);
    }
    public NotLoggedIn(final int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public void processExchange(final HttpServerExchange exchange) {
        respondError(exchange, FORBIDDEN, errorCode, "Not logged in");
    }

    public boolean isServerError() {
        return false;
    }

}
