package extension.undertow.server;

import extension.undertow.error.AccessDenied;
import extension.undertow.error.HttpError;
import io.undertow.predicate.Predicates;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.NameVirtualHostHandler;
import io.undertow.server.handlers.encoding.ContentEncodingRepository;
import io.undertow.server.handlers.encoding.DeflateEncodingProvider;
import io.undertow.server.handlers.encoding.EncodingHandler;
import io.undertow.server.handlers.encoding.GzipEncodingProvider;
import io.undertow.util.HttpString;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

import static extension.undertow.model.ContentType.text_html;
import static extension.undertow.server.CacheControlStrategy.NEVER_CACHE;
import static extension.undertow.server.CacheControlStrategy.STORE_BUT_CHECK_SERVER;
import static extension.undertow.server.RequestParser.getMandatoryString;
import static extension.undertow.server.ResponseBuilder.respond;
import static extension.undertow.server.ResponseBuilder.respondInternalError;
import static io.undertow.predicate.Predicates.requestLargerThan;
import static io.undertow.util.Headers.*;
import static io.undertow.util.StatusCodes.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public enum Handlers {;

    public static HttpHandler showPage(final JarResource page) throws IOException {
        return usingThread(staticHtml(page));
    }
    public static HttpHandler showPage(final JarResource page, final Set<String> knownURIs) throws IOException {
        return usingThread(singlePageApp(page, knownURIs));
    }
    public static HttpHandler show404(final JarResource page) throws IOException {
        return usingThread(html404(page));
    }

    public static HttpHandler logError(final Logger logger, final HttpHandler next) {
        return exchange -> {
            try {
                next.handleRequest(exchange);
            } catch (final Exception e) {
                if (e instanceof HttpError error)
                    error.processExchange(exchange);
                else {
                    logger.error(e);
                    respondInternalError(exchange);
                }
            } catch (final Error e) {
                logger.error(e);
                throw e;
            }
        };
    }

    public static HttpHandler logCspViolation(final Logger dao) {
        return exchange -> {
            final var violation = new String(exchange.getInputStream().readAllBytes(), UTF_8);
            dao.error("CSP violation:\n" + violation);
            respond(exchange).status(NO_CONTENT).send();
        };
    }

    public static HttpHandler trace(final Logger logger, final String message, final HttpHandler next) {
        return exchange -> {
            logger.info(message);
            next.handleRequest(exchange);
        };
    }

    public record CspEndpoint(String url) {}
    public record ReportTo(String group, int max_age, List<CspEndpoint> endpoints) {}
    public record CspSettings(String baseCsp, String styleSrc, String reportTo) {}

    public static HttpHandler singlePageApp(final JarResource resource, final Set<String> pageURIs) throws IOException {
        return singlePageApp(resource.asString(), pageURIs);
    }
    public static HttpHandler singlePageApp(final CspSettings csp, final JarResource resource, final Set<String> pageURIs) throws IOException {
        return singlePageApp(csp, resource.asString(), pageURIs);
    }
    public static HttpHandler singlePageApp(final String pageContent, final Set<String> pageURIs) {
        final var notFound = html404(pageContent);
        final var found = staticHtml(pageContent);
        return exchange -> (pageURIs.contains(exchange.getRequestPath()) ? found : notFound).handleRequest(exchange);
    }
    public static HttpHandler singlePageApp(final CspSettings csp, final String pageContent, final Set<String> pageURIs) {
        final var notFound = html404(pageContent);
        final var found = staticHtml(csp, pageContent);
        return exchange -> (pageURIs.contains(exchange.getRequestPath()) ? found : notFound).handleRequest(exchange);
    }

    public static HttpHandler staticHtml(final JarResource resource) throws IOException {
        return staticHtml(resource.asString());
    }
    public static HttpHandler staticHtml(final String content) {
        final String etag = String.format("\"%s\"", encodeHex(sha256(content, UTF_8)));

        return exchange -> {
            final String requestEtag = exchange.getRequestHeaders().getFirst(IF_NONE_MATCH);
            if (etag.equals(requestEtag)) {
                respond(exchange).status(NOT_MODIFIED).contentType(text_html).send();
            } else {
                respond(exchange).status(OK).contentType(text_html)
                    .cache(STORE_BUT_CHECK_SERVER)
                    .header(ETAG, etag).send(content);
            }
        };
    }
    public static HttpHandler staticHtml(final CspSettings csp, final JarResource resource) throws IOException {
        return staticHtml(csp, resource.asString());
    }
    public static HttpHandler staticHtml(final CspSettings csp, final String content) {
        final String etag = String.format("\"%s\"", encodeHex(sha256(content, UTF_8)));
        final String partialCsp = addHashesToCsp(csp.baseCsp, csp.styleSrc, content);
        return exchange -> {
            final String requestEtag = exchange.getRequestHeaders().getFirst(IF_NONE_MATCH);
            if (etag.equals(requestEtag)) {
                respond(exchange).status(NOT_MODIFIED).contentType(text_html).send();
            } else {
                final String nonce = newRandomCharacters(16, UPPERCASE_LOWERCASE_NUMBERS);
                final String page = content.replace("${CSP_NONCE}", nonce);
                final String cspHeader = addNoncesToCsp(partialCsp, "'nonce-" + nonce + "'");
                respond(exchange).status(OK).contentType(text_html)
                    .cache(STORE_BUT_CHECK_SERVER)
                    .header(REPORT_TO, csp.reportTo)
                    .header(CONTENT_SECURITY_POLICY, cspHeader)
                    .header(ETAG, etag).send(page);
            }
        };
    }

    public static HttpHandler usingThread(final HttpHandler next) {
        return exchange -> {
            exchange.startBlocking();
            if (exchange.isInIoThread()) {
                exchange.dispatch(next);
            } else {
                next.handleRequest(exchange);
            }
        };
    }

    public static HttpHandler usingThread(final Executor executor, final HttpHandler next) {
        return exchange -> {
            exchange.startBlocking();
            if (exchange.isInIoThread()) {
                exchange.dispatch(executor, next);
            } else {
                next.handleRequest(exchange);
            }
        };
    }

    public interface HttpEventCallback {
        void handleHttpEvent(String ip, String method, String uri, int statusCode);
    }

    public static HttpHandler recordEvent(final HttpEventCallback callback, final HttpHandler next) {
        return exchange -> {
            final String ip = getSourceIPAddress(exchange);
            final String method = exchange.getRequestMethod().toString();
            final String uri = exchange.getRequestURI();
            try {
                next.handleRequest(exchange);
            } catch (Exception e) {
                final int statusCode = exchange.getStatusCode();
                callback.handleHttpEvent(ip, method, uri, statusCode);
                throw e;
            }
        };
    }

    public static boolean matchesEtag(final HttpServerExchange request, final String etag) {
        return (etag.equals(request.getRequestHeaders().getFirst(IF_NONE_MATCH)));
    }

    public static void checkEtag(final String etag, final HttpServerExchange exchange, final HttpHandler next) throws Exception {
        if (matchesEtag(exchange, etag)) {
            respond(exchange).status(NOT_MODIFIED).send();
            return;
        }
        next.handleRequest(exchange);
    }

    public static HttpHandler compressResponse(final HttpHandler next) {
        return new EncodingHandler(new ContentEncodingRepository()
            .addEncodingHandler("gzip", new GzipEncodingProvider(), 100, Predicates.parse("max-content-size[5]"))
            .addEncodingHandler("deflate", new DeflateEncodingProvider(), 50, requestLargerThan(5)))
            .setNext(next);
    }

    public static HttpHandler resourceHandler(final String wwwroot, final HttpHandler fallback) {
        return exchange -> {
            final var requestURI = exchange.getRequestURI();
            final byte[] data = loadResource(wwwroot, requestURI);

            if (data == null) fallback.handleRequest(exchange);
            else {
                final var type = toMimeType(requestURI);
                final var etag = String.format("\"%s\"", encodeHex(sha256(data)));

                final String requestEtag = exchange.getRequestHeaders().getFirst(IF_NONE_MATCH);
                if (etag.equals(requestEtag)) {
                    respond(exchange).status(NOT_MODIFIED).cache(STORE_BUT_CHECK_SERVER).contentType(type).send();
                } else {
                    respond(exchange).status(OK).cache(STORE_BUT_CHECK_SERVER).contentType(type).header(ETAG, etag).send(data);
                }
            }
        };
    }

    public static byte[] loadResource(final String prefix, final String requestURI) throws IOException {
        if (requestURI.isBlank() || requestURI.endsWith("/") || requestURI.endsWith("\\")) return null;
        final var requestedPath = Path.of(prefix, requestURI).normalize().toString().replace('\\', '/');
        if (!requestedPath.startsWith(prefix)) return null;

        try (final InputStream in = Handlers.class.getResourceAsStream(requestedPath)) {
            if (in == null) return null;
            return in.readAllBytes();
        }
    }

    public static HttpHandler html404(final JarResource resource) throws IOException {
        return html404(resource.asString());
    }
    public static HttpHandler html404(final String html) {
        return exchange -> respond(exchange).status(NOT_FOUND)
            .cache(NEVER_CACHE).contentType(text_html).send(html);
    }

    public static HttpHandler statusCode(final int code) {
        return exchange -> exchange.setStatusCode(code);
    }

    public static HttpHandler redirect(final String url) {
        return exchange -> respond(exchange).status(FOUND).header(LOCATION, url).send();
    }

    public static HttpHandler noSuchApiEndpoint() {
        return exchange -> respondError(exchange, BAD_REQUEST, BAD_REQUEST, "No such API endpoint");
    }

    public static HttpHandler pathPrefix(final String pathPrefix, final HttpHandler match, final HttpHandler noMatch) {
        return exchange -> {
            final var choice = exchange.getRequestURI().startsWith(pathPrefix) ? match : noMatch;
            choice.handleRequest(exchange);
        };
    }

    public static ExtendedRoutingHandler routing() {
        return new ExtendedRoutingHandler();
    }
    public static <T> UserRoutingHandler<T> userRouting() {
        return new UserRoutingHandler<>();
    }

    public static NameVirtualHostHandler virtualHosts() {
        return new NameVirtualHostHandler();
    }

    public static HttpHandler isAuthorized(final String key, final HttpHandler next) {
        return exchange -> {
            final String requestKey = getMandatoryString(exchange, AUTHORIZATION);
            if (!key.equals(requestKey)) throw new AccessDenied("Invalid API key");
            next.handleRequest(exchange);
        };
    }
    public static HttpHandler isAuthorized(final Set<String> keys, final HttpHandler next) {
        return exchange -> {
            final String requestKey = getMandatoryString(exchange, AUTHORIZATION);
            if (!keys.contains(requestKey)) throw new AccessDenied("Invalid API key");
            next.handleRequest(exchange);
        };
    }

    private static final HttpString
        CONTENT_SECURITY_POLICY_REPORT_ONLY = HttpString.tryFromString("Content-Security-Policy-Report-Only"),
        CROSS_ORIGIN_OPENER_POLICY = HttpString.tryFromString("Cross-Origin-Opener-Policy");

    public static HttpHandler securityHeaders(final HttpHandler next) {
        return exchange -> {
            if (exchange.isSecure()) {
                exchange.getResponseHeaders()
                    .add(STRICT_TRANSPORT_SECURITY, "max-age=31536000; includeSubDomains")
                    .add(CROSS_ORIGIN_OPENER_POLICY, "same-origin");
            }
            exchange.getResponseHeaders()
                .add(X_FRAME_OPTIONS, "sameorigin")
                .add(X_XSS_PROTECTION, "1; mode=block")
                .add(X_CONTENT_TYPE_OPTIONS, "nosniff")
                .add(REFERRER_POLICY, "no-referrer");
            next.handleRequest(exchange);
        };
    }

    public static HttpHandler addCsp(final String value, final HttpHandler next) {
        return exchange -> {
            exchange.getResponseHeaders().add(CONTENT_SECURITY_POLICY, value);
            next.handleRequest(exchange);
        };
    }

    public static HttpHandler addCspReportOnly(final String value, final HttpHandler next) {
        return exchange -> {
            exchange.getResponseHeaders().add(CONTENT_SECURITY_POLICY_REPORT_ONLY, value);
            next.handleRequest(exchange);
        };
    }

}
