package extension.undertow.error;

import io.undertow.server.HttpServerExchange;

import java.io.IOException;

import static extension.undertow.server.ResponseBuilder.respondError;
import static io.undertow.util.StatusCodes.BAD_GATEWAY;

public class RemoteServiceFailure extends IOException implements HttpError {
    private final int errorCode;
    public RemoteServiceFailure(final String message) {
        this(BAD_GATEWAY, message);
    }
    public RemoteServiceFailure(final int errorCode, final String message) {
        this(errorCode, message, null);
    }
    public RemoteServiceFailure(final int errorCode, final String message, final Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    @Override
    public void processExchange(final HttpServerExchange exchange) {
        respondError(exchange, BAD_GATEWAY, errorCode, "External app.error");
    }

    public boolean isServerError() {
        return true;
    }
}
