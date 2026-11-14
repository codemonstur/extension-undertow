package extension.undertow.server;

import io.undertow.predicate.Predicate;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;

import static io.undertow.util.Methods.*;

/**
 * The {@code ExtendedRoutingHandler} class.
 */
public final class ExtendedRoutingHandler extends RoutingHandler {

    /**
     * Returns the result of {@code head}.
     *
     * @param template the template
     * @param handler the handler
     * @return the result
     */
    public ExtendedRoutingHandler head(final String template, final HttpHandler handler) {
        add(HEAD, template, handler);
        return this;
    }

    /**
     * Returns the result of {@code get}.
     *
     * @param template the template
     * @param handler the handler
     * @return the result
     */
    public ExtendedRoutingHandler get(final String template, final HttpHandler handler) {
        add(GET, template, handler);
        return this;
    }

    /**
     * Returns the result of {@code post}.
     *
     * @param template the template
     * @param handler the handler
     * @return the result
     */
    public ExtendedRoutingHandler post(final String template, final HttpHandler handler) {
        add(POST, template, handler);
        return this;
    }

    /**
     * Returns the result of {@code put}.
     *
     * @param template the template
     * @param handler the handler
     * @return the result
     */
    public ExtendedRoutingHandler put(final String template, final HttpHandler handler) {
        add(PUT, template, handler);
        return this;
    }

    /**
     * Returns the result of {@code delete}.
     *
     * @param template the template
     * @param handler the handler
     * @return the result
     */
    public ExtendedRoutingHandler delete(final String template, final HttpHandler handler) {
        add(DELETE, template, handler);
        return this;
    }

    /**
     * Returns the result of {@code head}.
     *
     * @param template the template
     * @param predicate the predicate
     * @param handler the handler
     * @return the result
     */
    public ExtendedRoutingHandler head(final String template
            , final Predicate predicate, final HttpHandler handler) {
        add(HEAD, template, predicate, handler);
        return this;
    }

    /**
     * Returns the result of {@code get}.
     *
     * @param template the template
     * @param predicate the predicate
     * @param handler the handler
     * @return the result
     */
    public ExtendedRoutingHandler get(final String template, final Predicate predicate, final HttpHandler handler) {
        add(GET, template, predicate, handler);
        return this;
    }

    /**
     * Returns the result of {@code post}.
     *
     * @param template the template
     * @param predicate the predicate
     * @param handler the handler
     * @return the result
     */
    public ExtendedRoutingHandler post(final String template, final Predicate predicate, final HttpHandler handler) {
        add(POST, template, predicate, handler);
        return this;
    }

    /**
     * Returns the result of {@code put}.
     *
     * @param template the template
     * @param predicate the predicate
     * @param handler the handler
     * @return the result
     */
    public ExtendedRoutingHandler put(final String template, final Predicate predicate, final HttpHandler handler) {
        add(PUT, template, predicate, handler);
        return this;
    }

    /**
     * Returns the result of {@code delete}.
     *
     * @param template the template
     * @param predicate the predicate
     * @param handler the handler
     * @return the result
     */
    public ExtendedRoutingHandler delete(final String template, final Predicate predicate, final HttpHandler handler) {
        add(DELETE, template, predicate, handler);
        return this;
    }

    /**
     * Returns the result of {@code fallback}.
     *
     * @param handler the handler
     * @return the result
     */
    public ExtendedRoutingHandler fallback(final HttpHandler handler) {
        setFallbackHandler(handler);
        return this;
    }


    /**
     * Creates a new instance.
     */
    public ExtendedRoutingHandler() {
    }
}
