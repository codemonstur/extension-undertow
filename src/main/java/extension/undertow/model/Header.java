package extension.undertow.model;

public interface Header {

    static Header newHeader(final String name) {
        return new Header() {
            public String toString() {
                return name;
            }
        };
    }

    Header AUTHORIZATION = newHeader("Authorization");
    Header CACHE_CONTROL = newHeader("Cache-Control");
    Header CONTENT_TYPE = newHeader("Content-Type");
    Header EXPIRES = newHeader("Expires");
    Header HOST = newHeader("Host");

}
