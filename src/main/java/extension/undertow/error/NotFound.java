package extension.undertow.error;

import io.undertow.server.HttpServerExchange;

import java.io.IOException;

import static io.undertow.util.StatusCodes.NOT_FOUND;

public final class NotFound extends IOException implements HttpError {
    public NotFound() {}
    public NotFound(final String message) {
        super(message);
    }

    @Override
    public void processExchange(final HttpServerExchange exchange) {
        exchange.setStatusCode(NOT_FOUND);
    }

    public boolean isServerError() {
        return false;
    }
}
