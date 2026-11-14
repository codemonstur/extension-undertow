package extension.undertow.server;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;

import java.util.function.Consumer;

import static io.undertow.util.Headers.*;

/**
 * The {@code CacheControlStrategy} enum.
 */
public enum CacheControlStrategy {
    /**
     * The {@code SET_NOTHING} constant.
     */
    SET_NOTHING(headers -> {}),
    /**
     * The {@code NEVER_CACHE} constant.
     */
    NEVER_CACHE(headers -> headers
        .add(CACHE_CONTROL, "no-cache, no-store, must-revalidate, pre-check=0, post-check=0, max-age=0, s-maxage=0")
        .add(EXPIRES, 0)
        .add(PRAGMA, "no-cache")),
    /**
     * The {@code STORE_BUT_CHECK_SERVER} constant.
     */
    STORE_BUT_CHECK_SERVER(headers -> headers
        .add(CACHE_CONTROL, "public, no-cache, max-age=0, must-revalidate")),
    /**
     * The {@code IMMUTABLE} constant.
     */
    IMMUTABLE(headers -> headers
        .add(CACHE_CONTROL, "public, max-age=315569260, immutable")
        .add(EXPIRES, "Fri, 1 Jan 2100 00:00:00 GMT"));

    private final Consumer<HeaderMap> impl;
    CacheControlStrategy(final Consumer<HeaderMap> impl) {
        this.impl = impl;
    }
    /**
     * Performs the {@code apply} operation.
     *
     * @param exchange the exchange
     */
    public void apply(final HttpServerExchange exchange) {
        impl.accept(exchange.getResponseHeaders());
    }
}