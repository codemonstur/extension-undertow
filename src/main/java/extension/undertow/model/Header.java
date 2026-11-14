package extension.undertow.model;

/**
 * The {@code Header} interface.
 */
public interface Header {

    /**
     * Returns the result of {@code newHeader}.
     *
     * @param name the name
     * @return the result
     */
    static Header newHeader(final String name) {
        return new Header() {
            public String toString() {
                return name;
            }
        };
    }

    /**
     * The {@code AUTHORIZATION} field.
     */
    Header AUTHORIZATION = newHeader("Authorization");
    /**
     * The {@code CACHE_CONTROL} field.
     */
    Header CACHE_CONTROL = newHeader("Cache-Control");
    /**
     * The {@code CONTENT_TYPE} field.
     */
    Header CONTENT_TYPE = newHeader("Content-Type");
    /**
     * The {@code EXPIRES} field.
     */
    Header EXPIRES = newHeader("Expires");
    /**
     * The {@code HOST} field.
     */
    Header HOST = newHeader("Host");

}
