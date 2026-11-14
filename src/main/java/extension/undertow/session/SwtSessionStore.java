package extension.undertow.session;

import extension.undertow.error.InvalidInput;
import extension.undertow.error.NotLoggedIn;
import io.undertow.server.HttpServerExchange;

import java.util.concurrent.TimeUnit;

import static extension.undertow.session.SessionStore.getValueForCookie;
import static java.lang.System.currentTimeMillis;

/**
 * The {@code SwtSessionStore} interface.
 *
 * @param <T> the type parameter
 */
public interface SwtSessionStore<T extends SWT> extends TokenSessionStore<T> {

    /**
     * Returns the session duration.
     *
     * @return the result
     */
    default long getSessionDuration() {
        return TimeUnit.MINUTES.toMillis(30);
    }
    /**
     * Returns the result of {@code renewAutomatically}.
     *
     * @return whether the condition holds
     */
    default boolean renewAutomatically() {
        return true;
    }

    /**
     * Returns the result of {@code getSession}.
     *
     * @param exchange the exchange
     * @return the result
     * @throws NotLoggedIn if the operation fails
     * @throws InvalidInput if the operation fails
     */
    default T getSession(final HttpServerExchange exchange) throws NotLoggedIn, InvalidInput {
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
     * @throws InvalidInput if the operation fails
     */
    default T getSession(final HttpServerExchange exchange, final T defaultValue) throws InvalidInput {
        final String sessionValue = getValueForCookie(exchange, getSessionCookieName());
        if (sessionValue == null) return defaultValue;

        T session = fromSessionValue(sessionValue);
        if (isExpired(session)) return defaultValue;
        if (!renewAutomatically()) return session;

        session = session.renew(currentTimeMillis() + getSessionDuration());
        setSession(exchange, session);
        return session;
    }

    private boolean isExpired(final T session) {
        return session.exp < (currentTimeMillis() - getSessionDuration());
    }

}
