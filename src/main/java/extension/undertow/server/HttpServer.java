package extension.undertow.server;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;

import static io.undertow.UndertowOptions.ENABLE_HTTP2;

/**
 * The {@code HttpServer} enum.
 */
public enum HttpServer {;

    /**
     * Returns the result of {@code newHttpServer}.
     *
     * @param address the address
     * @param port the port
     * @param hasHttp2 the has http2
     * @param handler the handler
     * @return the result
     */
    public static Undertow newHttpServer(final String address, final int port, final boolean hasHttp2, final HttpHandler handler) {
        return Undertow.builder()
            .setServerOption(ENABLE_HTTP2, hasHttp2)
            .addHttpListener(port, address)
            .setHandler(handler)
            .build();
    }

}
