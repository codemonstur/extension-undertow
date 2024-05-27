package extension.undertow.session;

import extension.undertow.error.InvalidInput;
import extension.undertow.error.NotLoggedIn;
import io.undertow.server.HttpServerExchange;

import static extension.undertow.session.SessionStore.getValueForCookie;
import static io.undertow.util.Headers.SET_COOKIE;
import static java.nio.charset.StandardCharsets.UTF_8;

public interface TokenSessionStore<T> extends SessionStore<T>, JsonSerializer {

    String getSessionValidationKey();
    Class<T> getSessionClass();

    default void setSession(final HttpServerExchange exchange, final T session) {
        final String sessionValue = toSessionValue(session);
        exchange.getResponseHeaders().add(SET_COOKIE, getSessionCookieName() + "=" + sessionValue + getSessionCookieConfiguration());
    }
    default boolean existsSession(final HttpServerExchange exchange) {
        try {
            return getSession(exchange, null) != null;
        } catch (final Exception e) {
            return false;
        }
    }
    default T getSession(final HttpServerExchange exchange) throws NotLoggedIn, InvalidInput {
        final T session = getSession(exchange, null);
        if (session == null) throw new NotLoggedIn();
        return session;
    }
    default T getSession(final HttpServerExchange exchange, final T defaultValue) throws InvalidInput {
        final String sessionValue = getValueForCookie(exchange, getSessionCookieName());
        if (sessionValue == null) return defaultValue;
        return fromSessionValue(sessionValue);
    }
    default void deleteSession(final HttpServerExchange exchange) {
        final String sessionId = getValueForCookie(exchange, getSessionCookieName());
        if (sessionId == null) return;
        exchange.getResponseHeaders().add(SET_COOKIE, getSessionCookieName() + "=" + getSessionCookieConfiguration());
    }

    private String toSessionValue(final T session) {
        final String encodedSession = encodeBase64Url(toJson(session).getBytes(UTF_8));
        return encodedSession + "." + toVerification(getSessionValidationKey(), encodedSession);
    }
    default T fromSessionValue(final String sessionValue) throws InvalidInput {
        final String[] encodedSession = sessionValue.split("\\.");
        if (isValidSession(encodedSession, getSessionValidationKey()))
            throw new InvalidInput("Invalid session");
        return fromJson(new String(decodeBase64Url(encodedSession[0]), UTF_8), getSessionClass());
    }

    private static boolean isValidSession(final String[] encodedSession, final String key) {
        return encodedSession.length == 2 && encodedSession[1].equals(toVerification(key, encodedSession[0]));
    }
    private static String toVerification(final String key, final String data) {
        return encodeBase64Url(hmacSha256(data, key));
    }

}
