package extension.undertow.model;

/**
 * The {@code ContentType} interface.
 */
public interface ContentType {

    /**
     * Returns the result of {@code newContentType}.
     *
     * @param type the type
     * @return the result
     */
    static ContentType newContentType(final String type) {
        return new ContentType() {
            public String toString() {
                return type;
            }
        };
    }

    /**
     * The {@code application_octetstream} constant.
     */
    /**
     * The {@code application_pdf} constant.
     */
    /**
     * The {@code application_json} constant.
     */
    /**
     * The {@code text_css} constant.
     */
    /**
     * The {@code text_plain} constant.
     */
    /**
     * The {@code text_html} constant.
     */
    public static final ContentType
        text_html = newContentType("text/html; charset=UTF-8"),
        text_plain = newContentType("text/plain; charset=UTF-8"),
        text_css = newContentType("text/css; charset=UTF-8"),
        application_json = newContentType("application/json"),
        application_pdf = newContentType("application/pdf"),
        application_octetstream = newContentType("application/octet-stream");
}
