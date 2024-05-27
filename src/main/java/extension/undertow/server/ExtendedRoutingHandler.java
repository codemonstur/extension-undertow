package extension.undertow.server;

import io.undertow.predicate.Predicate;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;

import static io.undertow.util.Methods.*;

public final class ExtendedRoutingHandler extends RoutingHandler {

    public ExtendedRoutingHandler head(final String template, final HttpHandler handler) {
        add(HEAD, template, handler);
        return this;
    }

    public ExtendedRoutingHandler get(final String template, final HttpHandler handler) {
        add(GET, template, handler);
        return this;
    }

    public ExtendedRoutingHandler post(final String template, final HttpHandler handler) {
        add(POST, template, handler);
        return this;
    }

    public ExtendedRoutingHandler put(final String template, final HttpHandler handler) {
        add(PUT, template, handler);
        return this;
    }

    public ExtendedRoutingHandler delete(final String template, final HttpHandler handler) {
        add(DELETE, template, handler);
        return this;
    }

    public ExtendedRoutingHandler head(final String template
            , final Predicate predicate, final HttpHandler handler) {
        add(HEAD, template, predicate, handler);
        return this;
    }

    public ExtendedRoutingHandler get(final String template, final Predicate predicate, final HttpHandler handler) {
        add(GET, template, predicate, handler);
        return this;
    }

    public ExtendedRoutingHandler post(final String template, final Predicate predicate, final HttpHandler handler) {
        add(POST, template, predicate, handler);
        return this;
    }

    public ExtendedRoutingHandler put(final String template, final Predicate predicate, final HttpHandler handler) {
        add(PUT, template, predicate, handler);
        return this;
    }

    public ExtendedRoutingHandler delete(final String template, final Predicate predicate, final HttpHandler handler) {
        add(DELETE, template, predicate, handler);
        return this;
    }

    public ExtendedRoutingHandler fallback(final HttpHandler handler) {
        setFallbackHandler(handler);
        return this;
    }

}
