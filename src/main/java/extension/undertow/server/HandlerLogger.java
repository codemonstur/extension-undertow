package extension.undertow.server;

public interface HandlerLogger {
    void error(Object message);
    void info(String message);
}
