package extension.undertow.session;

import extension.undertow.error.NotLoggedIn;
import io.undertow.server.HttpServerExchange;

import java.io.IOException;
import java.util.Random;

import static extension.undertow.session.SessionStore.getValueForCookie;
import static io.undertow.util.Headers.SET_COOKIE;

public interface RandomIdStore<T> extends SessionStore<T> {

    default Random prngSessionId() {
        return SECURE;
    }
    default int lengthSessionId() {
        return 32;
    }

    void storeSession(String sessionId, T session) throws IOException;
    T retrieveSession(String sessionId) throws IOException;
    void deleteSession(String sessionId) throws IOException;

    default void setSession(final HttpServerExchange exchange, final T session) throws IOException {
        final String sessionId = newByteArray().prng(prngSessionId()).length(lengthSessionId()).fillRandom().hex();
        storeSession(sessionId, session);
        exchange.getResponseHeaders().add(SET_COOKIE, getSessionCookieName() + "=" + sessionId + getSessionCookieConfiguration());
    }

    default boolean existsSession(final HttpServerExchange exchange) {
        try {
            return getSession(exchange, null) != null;
        } catch (final Exception e) {
            return false;
        }
    }

    default T getSession(final HttpServerExchange exchange) throws NotLoggedIn, IOException {
        final T session = getSession(exchange, null);
        if (session == null) throw new NotLoggedIn();
        return session;
    }

    default T getSession(final HttpServerExchange exchange, final T defaultValue) throws IOException {
        final String sessionId = getValueForCookie(exchange, getSessionCookieName());
        if (sessionId == null) return defaultValue;

        return retrieveSession(sessionId);
    }

    default void deleteSession(final HttpServerExchange exchange) throws IOException {
        final String sessionId = getValueForCookie(exchange, getSessionCookieName());
        if (sessionId == null) return;

        deleteSession(sessionId);
        exchange.getResponseHeaders().add(SET_COOKIE, getSessionCookieName() + "=" + getSessionCookieConfiguration());
    }

}
