package extension.undertow.session;

import java.io.Reader;
import java.lang.reflect.Type;

public interface JsonSerializer {
    String toJson(Object o);
    <T> T fromJson(String json, Class<T> clazz);
    <T> T fromJson(String json, Type type);
    <T> T fromJson(Reader json, Class<T> clazz);
}
