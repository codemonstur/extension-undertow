package extension.undertow.model;

public interface ContentType {

    static ContentType newContentType(final String type) {
        return new ContentType() {
            public String toString() {
                return type;
            }
        };
    }

    public static final ContentType
        text_html = newContentType("text/html; charset=UTF-8"),
        text_plain = newContentType("text/plain; charset=UTF-8"),
        text_css = newContentType("text/css; charset=UTF-8"),
        application_json = newContentType("application/json"),
        application_pdf = newContentType("application/pdf"),
        application_octetstream = newContentType("application/octet-stream");
}
