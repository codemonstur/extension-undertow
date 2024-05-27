package extension.undertow.session;

import extension.undertow.error.AccessDenied;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

import static extension.undertow.model.ContentType.application_json;
import static extension.undertow.model.ContentType.text_plain;
import static extension.undertow.server.ResponseBuilder.respond;
import static io.undertow.util.StatusCodes.NO_CONTENT;
import static io.undertow.util.StatusCodes.OK;

public enum SessionHandlers {;

    // Basically an io.undertow.server.HttpHandler, but with the session added to it
    public interface UserRequestHandler<T> {
        void handleRequest(T session, HttpServerExchange exchange) throws Exception;
    }

    private static final HttpString CSRF_TOKEN_HEADER = HttpString.tryFromString("CSRF-Token");

    public static <T extends Session> HttpHandler hasSession(
            final SessionStore<T> store, final HttpHandler anonymous,
            final UserRequestHandler<T> authenticated) {
        return exchange -> {
            final T session = store.getSession(exchange, null);
            if (session == null) {
                anonymous.handleRequest(exchange);
            } else {
                authenticated.handleRequest(session, exchange);
            }
        };
    }

    public static <T extends Session> UserRequestHandler<T> hasCsrfToken(final UserRequestHandler<T> next) {
        return (session, exchange) -> {
            final String requestToken = exchange.getRequestHeaders().getFirst(CSRF_TOKEN_HEADER);
            if (requestToken == null || requestToken.isEmpty()) throw new AccessDenied("Missing CSRF Token");
            if (!requestToken.equals(session.csrfToken())) throw new AccessDenied("Invalid CSRF Token");

            next.handleRequest(session, exchange);
        };
    }

    private static final String
        STATUS_LOGGED_IN = "{ \"loggedIn\": true }",
        STATUS_LOGGED_OUT = "{ \"loggedIn\": false }";

    public static <T> UserRequestHandler<T> loggedIn() {
        return (session, exchange) ->
            respond(exchange).status(OK).contentType(application_json).send(STATUS_LOGGED_IN);
    }
    public static HttpHandler notLoggedIn() {
        return exchange -> respond(exchange).status(OK).contentType(application_json).send(STATUS_LOGGED_OUT);
    }
    public static <T extends Session> UserRequestHandler<T> csrfToken() {
        return (session, exchange) -> respond(exchange).status(OK).contentType(text_plain).send(session.csrfToken());
    }

    public static <T> UserRequestHandler<T> logoutUser(final SessionStore<? extends Session> dao) {
        return (session, exchange) -> {
            dao.deleteSession(exchange);
            exchange.setStatusCode(NO_CONTENT);
        };
    }

    public static <T> UserRequestHandler<T> ignoreSession(final HttpHandler next) {
        return (session, exchange) -> next.handleRequest(exchange);
    }

    public static HttpHandler accessDenied(final String message) {
        return exchange -> {
            throw new AccessDenied(message);
        };
    }

}
