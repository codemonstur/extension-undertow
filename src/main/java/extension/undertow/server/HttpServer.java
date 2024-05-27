package extension.undertow.server;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;

import static io.undertow.UndertowOptions.ENABLE_HTTP2;

public enum HttpServer {;

    public static Undertow newHttpServer(final String address, final int port, final boolean hasHttp2, final HttpHandler handler) {
        return Undertow.builder()
            .setServerOption(ENABLE_HTTP2, hasHttp2)
            .addHttpListener(port, address)
            .setHandler(handler)
            .build();
    }

}
