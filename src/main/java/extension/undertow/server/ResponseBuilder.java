package extension.undertow.server;

import extension.undertow.model.ContentType;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static extension.undertow.server.CacheControlStrategy.NEVER_CACHE;
import static io.undertow.util.Headers.CONTENT_TYPE;
import static io.undertow.util.Headers.LOCATION;
import static io.undertow.util.StatusCodes.FOUND;
import static io.undertow.util.StatusCodes.INTERNAL_SERVER_ERROR;
import static java.nio.file.Files.newInputStream;

/**
 * The {@code ResponseBuilder} class.
 */
public final class ResponseBuilder {

    private static final String
        RESPONSE_INTERNAL_ERROR = "{\"success\":false,\"code\":500,\"message\":\"Internal Error\"}";
    private static final String
        ERROR_MESSAGE = "{\"success\":false,\"code\":%d,\"errorCode\":%d,\"message\":\"%s\"}";

    /**
     * Performs the {@code respondInternalError} operation.
     *
     * @param exchange the exchange
     */
    public static void respondInternalError(final HttpServerExchange exchange) {
        if (!exchange.isDispatched()) {
            exchange.setStatusCode(INTERNAL_SERVER_ERROR);
            exchange.getResponseHeaders().add(CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(RESPONSE_INTERNAL_ERROR);
        }
    }
    /**
     * Performs the {@code respondError} operation.
     *
     * @param exchange the exchange
     * @param statusCode the status code
     * @param errorCode the error code
     * @param message the message
     */
    public static void respondError(final HttpServerExchange exchange, final int statusCode, final int errorCode, final String message) {
        if (!exchange.isDispatched()) {
            exchange.setStatusCode(statusCode);
            exchange.getResponseHeaders().add(CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(errorMessage(statusCode, errorCode, message));
        }
    }

    /**
     * Returns the result of {@code errorMessage}.
     *
     * @param statusCode the status code
     * @param errorCode the error code
     * @param message the message
     * @return the result
     */
    public static String errorMessage(final int statusCode, final int errorCode, final String message) {
        return String.format(ERROR_MESSAGE, statusCode, errorCode, escapeJson(message));
    }

    private static String escapeJson(final String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /**
     * Returns the result of {@code respond}.
     *
     * @param exchange the exchange
     * @return the result
     */
    public static ResponseBuilder respond(final HttpServerExchange exchange) {
        return new ResponseBuilder(exchange);
    }

    private final HttpServerExchange exchange;
    private final Map<HttpString, String> headers = new HashMap<>();
    private int status = INTERNAL_SERVER_ERROR;
    private CacheControlStrategy caching = NEVER_CACHE;

    private ResponseBuilder(final HttpServerExchange exchange) {
        this.exchange = exchange;
    }
    /**
     * Returns the result of {@code status}.
     *
     * @param status the status
     * @return the result
     */
    public ResponseBuilder status(final int status) {
        if (status < 100 || status > 999) throw new IllegalArgumentException("Status must be a valid HTTP status code");
        this.status = status;
        return this;
    }
    /**
     * Returns the result of {@code cache}.
     *
     * @param caching the caching
     * @return the result
     */
    public ResponseBuilder cache(final CacheControlStrategy caching) {
        this.caching = caching;
        return this;
    }
    /**
     * Returns the result of {@code header}.
     *
     * @param name the name
     * @param value the value
     * @return the result
     */
    public ResponseBuilder header(final String name, final String value) {
        return header(HttpString.tryFromString(name), value);
    }
    /**
     * Returns the result of {@code header}.
     *
     * @param name the name
     * @param value the value
     * @return the result
     */
    public ResponseBuilder header(final HttpString name, final String value) {
        this.headers.put(name, value);
        return this;
    }
    /**
     * Returns the result of {@code header}.
     *
     * @param name the name
     * @param value the value
     * @return the result
     */
    public ResponseBuilder header(final String name, final int value) {
        return header(HttpString.tryFromString(name), value);
    }
    /**
     * Returns the result of {@code header}.
     *
     * @param name the name
     * @param value the value
     * @return the result
     */
    public ResponseBuilder header(final HttpString name, final int value) {
        this.headers.put(name, Integer.toString(value));
        return this;
    }
    /**
     * Returns the result of {@code header}.
     *
     * @param name the name
     * @param value the value
     * @return the result
     */
    public ResponseBuilder header(final String name, final long value) {
        return header(HttpString.tryFromString(name), value);
    }
    /**
     * Returns the result of {@code header}.
     *
     * @param name the name
     * @param value the value
     * @return the result
     */
    public ResponseBuilder header(final HttpString name, final long value) {
        this.headers.put(name, Long.toString(value));
        return this;
    }
    /**
     * Returns the result of {@code contentType}.
     *
     * @param type the type
     * @return the result
     */
    public ResponseBuilder contentType(final String type) {
        this.headers.put(CONTENT_TYPE, type);
        return this;
    }
    /**
     * Returns the result of {@code contentType}.
     *
     * @param type the type
     * @return the result
     */
    public ResponseBuilder contentType(final ContentType type) {
        this.headers.put(CONTENT_TYPE, type.toString());
        return this;
    }
    /**
     * Returns the result of {@code redirect}.
     *
     * @param url the url
     * @return the result
     */
    public ResponseBuilder redirect(final String url) {
        this.status = FOUND;
        this.headers.put(LOCATION, url);
        return this;
    }

    private void preSend() {
        exchange.setStatusCode(status);
        if (status >= 200 && status <= 299) {
            caching.apply(exchange);
        }
        final var responseHeaders = exchange.getResponseHeaders();
        for (final var entry : headers.entrySet()) {
            responseHeaders.add(entry.getKey(), entry.getValue());
        }
    }
    /**
     * Performs the {@code send} operation.
     *
     * @param data the data
     */
    public void send(final String data) {
        preSend();
        exchange.getResponseSender().send(data);
    }

    /**
     * Performs the {@code send} operation.
     *
     * @param data the data
     */
    public void send(final byte[] data) {
        preSend();
        exchange.getResponseSender().send(ByteBuffer.wrap(data));
    }
    /**
     * Performs the {@code send} operation.
     *
     * @param buffer the buffer
     */
    public void send(final ByteBuffer buffer) {
        preSend();
        exchange.getResponseSender().send(buffer);
    }
    /**
     * Performs the {@code send} operation.
     *
     * @param in the in
     * @throws IOException if the operation fails
     */
    public void send(final InputStream in) throws IOException {
        preSend();
        in.transferTo(exchange.getOutputStream());
    }
    /**
     * Performs the {@code send} operation.
     *
     * @param path the path
     * @throws IOException if the operation fails
     */
    public void send(final Path path) throws IOException {
        preSend();
        try (final var in = newInputStream(path)) {
            in.transferTo(exchange.getOutputStream());
        }
    }

    /**
     * Performs the {@code send} operation.
     */
    public void send() {
        preSend();
    }

}
