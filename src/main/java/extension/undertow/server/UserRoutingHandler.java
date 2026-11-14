package extension.undertow.server;

import extension.undertow.session.SessionHandlers.UserRequestHandler;
import io.undertow.predicate.Predicate;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.CopyOnWriteMap;
import io.undertow.util.HttpString;
import io.undertow.util.PathTemplateMatch;
import io.undertow.util.PathTemplateMatcher;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static io.undertow.util.Methods.*;
import static io.undertow.util.StatusCodes.NOT_FOUND;

/**
 * The {@code UserRoutingHandler} class.
 *
 * @param <T> the type parameter
 */
public class UserRoutingHandler<T> implements UserRequestHandler<T> {

    // Matcher objects grouped by http methods.
    private final Map<HttpString, PathTemplateMatcher<RoutingMatch<T>>> matches = new CopyOnWriteMap<>();
    // Matcher used to find if this instance contains matches for any http method for a path.
    // This matcher is used to report if this instance can match a path for one of the http methods.
    private final PathTemplateMatcher<RoutingMatch<T>> allMethodsMatcher = new PathTemplateMatcher<>();

    // Handler called when no match was found and invalid method handler can't be invoked.
    private volatile HttpHandler fallbackHandler = exchange -> exchange.setStatusCode(NOT_FOUND);

    // If this is true then path matches will be added to the query parameters for easy access by later handlers.
    private final boolean rewriteQueryParameters;

    /**
     * Creates a new instance.
     */
    public UserRoutingHandler() {
        this.rewriteQueryParameters = true;
    }

    /**
     * Performs the {@code handleRequest} operation.
     *
     * @param session the session
     * @param exchange the exchange
     * @throws Exception if the operation fails
     */
    @Override
    public void handleRequest(final T session, final HttpServerExchange exchange) throws Exception {
        final var matcher = matches.get(exchange.getRequestMethod());
        if (matcher == null) {
            fallbackHandler.handleRequest(exchange);
            return;
        }
        final var match = matcher.match(exchange.getRelativePath());
        if (match == null) {
            fallbackHandler.handleRequest(exchange);
            return;
        }
        exchange.putAttachment(PathTemplateMatch.ATTACHMENT_KEY, match);
        if (rewriteQueryParameters) {
            for (final var entry : match.getParameters().entrySet()) {
                exchange.addQueryParam(entry.getKey(), entry.getValue());
            }
        }
        for (final var handler : match.getValue().predicatedHandlers) {
            if (handler.predicate.resolve(exchange)) {
                handler.handler.handleRequest(session, exchange);
                return;
            }
        }
        if (match.getValue().defaultHandler != null) {
            match.getValue().defaultHandler.handleRequest(session, exchange);
        } else {
            fallbackHandler.handleRequest(exchange);
        }
    }

    /**
     * Returns the result of {@code add}.
     *
     * @param method the method
     * @param template the template
     * @param handler the handler
     * @return the result
     */
    public synchronized UserRoutingHandler<T> add(final String method, final String template, final UserRequestHandler<T> handler) {
        return add(new HttpString(method), template, handler);
    }

    /**
     * Returns the result of {@code add}.
     *
     * @param method the method
     * @param template the template
     * @param handler the handler
     * @return the result
     */
    public synchronized UserRoutingHandler<T> add(final HttpString method, final String template, final UserRequestHandler<T> handler) {
        var matcher = matches.get(method);
        if (matcher == null) {
            matches.put(method, matcher = new PathTemplateMatcher<>());
        }
        var res = matcher.get(template);
        if (res == null) {
            matcher.add(template, res = new RoutingMatch<T>());
        }
        if (allMethodsMatcher.match(template) == null) {
            allMethodsMatcher.add(template, res);
        }
        res.defaultHandler = handler;
        return this;
    }

    /**
     * Returns the result of {@code head}.
     *
     * @param template the template
     * @param handler the handler
     * @return the result
     */
    public synchronized UserRoutingHandler<T> head(final String template, final UserRequestHandler<T> handler) {
        return add(HEAD, template, handler);
    }

    /**
     * Returns the result of {@code get}.
     *
     * @param template the template
     * @param handler the handler
     * @return the result
     */
    public synchronized UserRoutingHandler<T> get(final String template, final UserRequestHandler<T> handler) {
        return add(GET, template, handler);
    }

    /**
     * Returns the result of {@code post}.
     *
     * @param template the template
     * @param handler the handler
     * @return the result
     */
    public synchronized UserRoutingHandler<T> post(final String template, final UserRequestHandler<T> handler) {
        return add(POST, template, handler);
    }

    /**
     * Returns the result of {@code put}.
     *
     * @param template the template
     * @param handler the handler
     * @return the result
     */
    public synchronized UserRoutingHandler<T> put(final String template, final UserRequestHandler<T> handler) {
        return add(PUT, template, handler);
    }

    /**
     * Returns the result of {@code delete}.
     *
     * @param template the template
     * @param handler the handler
     * @return the result
     */
    public synchronized UserRoutingHandler<T> delete(final String template, final UserRequestHandler<T> handler) {
        return add(DELETE, template, handler);
    }

    /**
     * Returns the result of {@code add}.
     *
     * @param method the method
     * @param template the template
     * @param predicate the predicate
     * @param handler the handler
     * @return the result
     */
    public synchronized UserRoutingHandler<T> add(final String method, final String template,
                                                  final Predicate predicate, final UserRequestHandler<T> handler) {
        return add(new HttpString(method), template, predicate, handler);
    }

    /**
     * Returns the result of {@code add}.
     *
     * @param method the method
     * @param template the template
     * @param predicate the predicate
     * @param handler the handler
     * @return the result
     */
    public synchronized UserRoutingHandler<T> add(final HttpString method, final String template,
                                                  final Predicate predicate, final UserRequestHandler<T> handler) {
        PathTemplateMatcher<RoutingMatch<T>> matcher = matches.get(method);
        if (matcher == null) {
            matches.put(method, matcher = new PathTemplateMatcher<>());
        }
        RoutingMatch<T> res = matcher.get(template);
        if (res == null) {
            matcher.add(template, res = new RoutingMatch<>());
        }
        if (allMethodsMatcher.match(template) == null) {
            allMethodsMatcher.add(template, res);
        }
        res.predicatedHandlers.add(new HandlerHolder<>(predicate, handler));
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
    public synchronized UserRoutingHandler<T> head(final String template, final Predicate predicate,
                                                   final UserRequestHandler<T> handler) {
        return add(HEAD, template, predicate, handler);
    }

    /**
     * Returns the result of {@code get}.
     *
     * @param template the template
     * @param predicate the predicate
     * @param handler the handler
     * @return the result
     */
    public synchronized UserRoutingHandler<T> get(final String template, final Predicate predicate,
                                                  final UserRequestHandler<T> handler) {
        return add(GET, template, predicate, handler);
    }

    /**
     * Returns the result of {@code post}.
     *
     * @param template the template
     * @param predicate the predicate
     * @param handler the handler
     * @return the result
     */
    public synchronized UserRoutingHandler<T> post(final String template, final Predicate predicate,
                                                   final UserRequestHandler<T> handler) {
        return add(POST, template, predicate, handler);
    }

    /**
     * Returns the result of {@code put}.
     *
     * @param template the template
     * @param predicate the predicate
     * @param handler the handler
     * @return the result
     */
    public synchronized UserRoutingHandler<T> put(final String template, final Predicate predicate,
                                                  final UserRequestHandler<T> handler) {
        return add(PUT, template, predicate, handler);
    }

    /**
     * Returns the result of {@code delete}.
     *
     * @param template the template
     * @param predicate the predicate
     * @param handler the handler
     * @return the result
     */
    public synchronized UserRoutingHandler<T> delete(final String template, final Predicate predicate,
                                                     final UserRequestHandler<T> handler) {
        return add(DELETE, template, predicate, handler);
    }

    /**
     * Returns the result of {@code fallback}.
     *
     * @param fallback the fallback
     * @return the result
     */
    public synchronized UserRoutingHandler<T> fallback(final HttpHandler fallback) {
        this.fallbackHandler = fallback;
        return this;
    }

    private static class RoutingMatch<T> {
        final List<HandlerHolder<T>> predicatedHandlers = new CopyOnWriteArrayList<>();
        volatile UserRequestHandler<T> defaultHandler;
    }
    private record HandlerHolder<T>(Predicate predicate, UserRequestHandler<T> handler) {}

}
