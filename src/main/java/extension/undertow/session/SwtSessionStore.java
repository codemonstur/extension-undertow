package extension.undertow.session;

import extension.undertow.error.InvalidInput;
import extension.undertow.error.NotLoggedIn;
import io.undertow.server.HttpServerExchange;

import java.util.concurrent.TimeUnit;

import static extension.undertow.session.SessionStore.getValueForCookie;
import static java.lang.System.currentTimeMillis;

public interface SwtSessionStore<T extends SWT> extends TokenSessionStore<T> {

    default long getSessionDuration() {
        return TimeUnit.MINUTES.toMillis(30);
    }
    default boolean renewAutomatically() {
        return true;
    }

    default T getSession(final HttpServerExchange exchange) throws NotLoggedIn, InvalidInput {
        final T session = getSession(exchange, null);
        if (session == null) throw new NotLoggedIn();
        return session;
    }
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
