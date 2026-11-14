package extension.undertow.session;

import extension.undertow.error.InvalidInput;
import extension.undertow.error.NotLoggedIn;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;

import java.io.IOException;

import static io.undertow.util.Headers.COOKIE;

/**
 * The {@code SessionStore} interface.
 *
 * @param <T> the type parameter
 */
public interface SessionStore<T> {

    /**
     * Returns the session cookie name.
     *
     * @return the result
     */
    default String getSessionCookieName() {
        return "session";
    }
    /**
     * Returns the session cookie configuration.
     *
     * @return the result
     */
    default String getSessionCookieConfiguration() {
        return "; Path=/; Secure; HttpOnly; SameSite=strict";
    }

    /**
     * Performs the {@code setSession} operation.
     *
     * @param exchange the new value
     * @param session the new value
     * @throws IOException if the operation fails
     */
    void setSession(final HttpServerExchange exchange, final T session) throws IOException;
    /**
     * Returns the result of {@code existsSession}.
     *
     * @param exchange the exchange
     * @return whether the condition holds
     */
    boolean existsSession(final HttpServerExchange exchange);
    /**
     * Returns the result of {@code getSession}.
     *
     * @param exchange the exchange
     * @return the result
     * @throws NotLoggedIn if the operation fails
     * @throws IOException if the operation fails
     * @throws InvalidInput if the operation fails
     */
    T getSession(final HttpServerExchange exchange) throws NotLoggedIn, IOException, InvalidInput;
    /**
     * Returns the result of {@code getSession}.
     *
     * @param exchange the exchange
     * @param defaultValue the default value
     * @return the result
     * @throws IOException if the operation fails
     * @throws InvalidInput if the operation fails
     */
    T getSession(final HttpServerExchange exchange, final T defaultValue) throws IOException, InvalidInput;
    /**
     * Performs the {@code deleteSession} operation.
     *
     * @param exchange the exchange
     * @throws IOException if the operation fails
     */
    void deleteSession(final HttpServerExchange exchange) throws IOException;

    /*
    * Can also be implemented as:
    *   final var cookie = exchange.getRequestCookies().get(cookieName);
    *   return cookie == null ? null : cookie.getValue();
    * That code caches the cookies, but it also stores them all fully parsed in a map
    */
    /**
     * Returns the result of {@code getValueForCookie}.
     *
     * @param exchange the exchange
     * @param cookieName the cookie name
     * @return the result
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
