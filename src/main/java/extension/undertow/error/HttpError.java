package extension.undertow.error;

import io.undertow.server.HttpServerExchange;

public interface HttpError {
    void processExchange(HttpServerExchange exchange);
    boolean isServerError();

    public interface ServerHttpError extends HttpError {
        default boolean isServerError() {
            return true;
        }
    }
    public interface ClientHttpError extends HttpError {
        default boolean isServerError() {
            return false;
        }
    }
}
