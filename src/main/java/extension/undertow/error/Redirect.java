package extension.undertow.error;

import io.undertow.server.HttpServerExchange;

import static extension.undertow.server.ResponseBuilder.respond;
import static io.undertow.util.Headers.LOCATION;
import static io.undertow.util.StatusCodes.FOUND;

public final class Redirect extends Exception implements HttpError {

    private final String header;
    public Redirect(final String header) {
        this.header = header;
    }

    public void processExchange(final HttpServerExchange exchange) {
        respond(exchange).status(FOUND).header(LOCATION, header).send();
    }
    public boolean isServerError() {
        return false;
    }

}
