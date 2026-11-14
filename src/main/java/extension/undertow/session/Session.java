package extension.undertow.session;

/**
 * The {@code Session} interface.
 */
public interface Session {
    /**
     * Returns the result of {@code csrfToken}.
     *
     * @return the result
     */
    String csrfToken();
}
