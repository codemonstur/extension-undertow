package extension.undertow.session;

/**
 * The {@code SWT} class.
 */
public abstract class SWT implements Session {

    /**
     * The {@code exp} field.
     */
    public final long exp;
    /**
     * Creates a new instance.
     *
     * @param expiresAt the expires at
     */
    protected SWT(final long expiresAt) {
        this.exp = expiresAt;
    }

    /**
     * Returns the result of {@code renew}.
     *
     * @param <T> the type parameter
     * @param expiresAt the expires at
     * @return the result
     */
    public abstract <T extends SWT> T renew(long expiresAt);
}
