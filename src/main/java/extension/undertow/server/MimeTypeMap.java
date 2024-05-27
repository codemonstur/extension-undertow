package extension.undertow.server;

import java.util.Map;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;

public enum MimeTypeMap {;

    private static final Map<String, String> mimetypeMap = ofEntries(
            entry(".ico", "image/x-icon"),
            entry(".gif", "image/gif"),
            entry(".jpg", "image/jpeg"),
            entry(".jpeg", "image/jpeg"),
            entry(".png", "image/png"),
            entry(".svg", "image/svg+xml"),
            entry(".htm", "text/html"),
            entry(".html", "text/html"),
            entry(".json", "application/json"),
            entry(".pdf", "application/pdf")
    );

    public static String toMimeType(final String filename) {
        final var offset = filename.lastIndexOf('.');
        if (offset == -1) return "application/octet-stream";
        final var extension = filename.substring(offset);
        return mimetypeMap.getOrDefault(extension, "application/octet-stream");
    }

}
