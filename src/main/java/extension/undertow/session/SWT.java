package extension.undertow.session;

public abstract class SWT implements Session {

    public final long exp;
    protected SWT(final long expiresAt) {
        this.exp = expiresAt;
    }

    public abstract <T extends SWT> T renew(long expiresAt);
}
