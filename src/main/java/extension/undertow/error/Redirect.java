package extension.undertow.error;

import io.undertow.server.HttpServerExchange;

import static extension.undertow.server.ResponseBuilder.respond;
import static io.undertow.util.Headers.LOCATION;
import static io.undertow.util.StatusCodes.FOUND;

/**
 * The {@code Redirect} class.
 */
public final class Redirect extends Exception implements HttpError {

    /**
     * The {@code header} field.
     */
    private final String header;
    /**
     * Creates a new instance.
     *
     * @param header the header
     */
    public Redirect(final String header) {
        this.header = header;
    }

    /**
     * Performs the {@code processExchange} operation.
     *
     * @param exchange the exchange
     */
    public void processExchange(final HttpServerExchange exchange) {
        respond(exchange).status(FOUND).header(LOCATION, header).send();
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
