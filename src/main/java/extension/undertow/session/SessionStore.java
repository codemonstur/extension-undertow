package extension.undertow.session;

import extension.undertow.error.InvalidInput;
import extension.undertow.error.NotLoggedIn;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;

import java.io.IOException;

import static io.undertow.util.Headers.COOKIE;

public interface SessionStore<T> {

    default String getSessionCookieName() {
        return "session";
    }
    default String getSessionCookieConfiguration() {
        return "; Path=/; Secure; HttpOnly; SameSite=strict";
    }

    void setSession(final HttpServerExchange exchange, final T session) throws IOException;
    boolean existsSession(final HttpServerExchange exchange);
    T getSession(final HttpServerExchange exchange) throws NotLoggedIn, IOException, InvalidInput;
    T getSession(final HttpServerExchange exchange, final T defaultValue) throws IOException, InvalidInput;
    void deleteSession(final HttpServerExchange exchange) throws IOException;

    /*
    * Can also be implemented as:
    *   final var cookie = exchange.getRequestCookies().get(cookieName);
    *   return cookie == null ? null : cookie.getValue();
    * That code caches the cookies, but it also stores them all fully parsed in a map
    */
    public static String getValueForCookie(final HttpServerExchange exchange, final String cookieName) {
        final HeaderValues headers = exchange.getRequestHeaders().get(COOKIE);
        if (headers == null || headers.isEmpty()) return null;

        final String cookiePrefix = cookieName + "=";
        for (final String header : headers) {
            for (final String cookie: header.split(";")) {
                if (cookie.trim().startsWith(cookiePrefix)) {
                    return cookie.substring(cookie.indexOf('=')+1).trim();
                }
            }
        }
        return null;
    }

}
