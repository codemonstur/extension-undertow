package extension.undertow.session;

import extension.undertow.error.NotLoggedIn;
import io.undertow.server.HttpServerExchange;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;

import static extension.undertow.session.SessionStore.getValueForCookie;
import static io.undertow.util.Headers.SET_COOKIE;

/**
 * The {@code RandomIdStore} interface.
 *
 * @param <T> the type parameter
 */
public interface RandomIdStore<T> extends SessionStore<T> {

    /**
     * Returns the result of {@code prngSessionId}.
     *
     * @return the result
     */
    default Random prngSessionId() {
        return new SecureRandom();
    }
    /**
     * Returns the result of {@code lengthSessionId}.
     *
     * @return the result
     */
    default int lengthSessionId() {
        return 32;
    }

    /**
     * Performs the {@code storeSession} operation.
     *
     * @param sessionId the session id
     * @param session the session
     * @throws IOException if the operation fails
     */
    void storeSession(String sessionId, T session) throws IOException;
    /**
     * Returns the result of {@code retrieveSession}.
     *
     * @param sessionId the session id
     * @return the result
     * @throws IOException if the operation fails
     */
    T retrieveSession(String sessionId) throws IOException;
    /**
     * Performs the {@code deleteSession} operation.
     *
     * @param sessionId the session id
     * @throws IOException if the operation fails
     */
    void deleteSession(String sessionId) throws IOException;

    /**
     * Performs the {@code setSession} operation.
     *
     * @param exchange the new value
     * @param session the new value
     * @throws IOException if the operation fails
     */
    default void setSession(final HttpServerExchange exchange, final T session) throws IOException {
        final byte[] bytes = new byte[lengthSessionId()];
        prngSessionId().nextBytes(bytes);
        final String sessionId = java.util.HexFormat.of().formatHex(bytes);
        storeSession(sessionId, session);
        exchange.getResponseHeaders().add(SET_COOKIE, getSessionCookieName() + "=" + sessionId + getSessionCookieConfiguration());
    }

    /**
     * Returns the result of {@code existsSession}.
     *
     * @param exchange the exchange
     * @return whether the condition holds
     */
    default boolean existsSession(final HttpServerExchange exchange) {
        try {
            return getSession(exchange, null) != null;
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * Returns the result of {@code getSession}.
     *
     * @param exchange the exchange
     * @return the result
     * @throws NotLoggedIn if the operation fails
     * @throws IOException if the operation fails
     */
    default T getSession(final HttpServerExchange exchange) throws NotLoggedIn, IOException {
        final T session = getSession(exchange, null);
        if (session == null) throw new NotLoggedIn();
        return session;
    }

    /**
     * Returns the result of {@code getSession}.
     *
     * @param exchange the exchange
     * @param defaultValue the default value
     * @return the result
     * @throws IOException if the operation fails
     */
    default T getSession(final HttpServerExchange exchange, final T defaultValue) throws IOException {
        final String sessionId = getValueForCookie(exchange, getSessionCookieName());
        if (sessionId == null) return defaultValue;

        return retrieveSession(sessionId);
    }

    /**
     * Performs the {@code deleteSession} operation.
     *
     * @param exchange the exchange
     * @throws IOException if the operation fails
     */
    default void deleteSession(final HttpServerExchange exchange) throws IOException {
        final String sessionId = getValueForCookie(exchange, getSessionCookieName());
        if (sessionId == null) return;

        deleteSession(sessionId);
        exchange.getResponseHeaders().add(SET_COOKIE, getSessionCookieName() + "=" + getSessionCookieConfiguration());
    }

}
