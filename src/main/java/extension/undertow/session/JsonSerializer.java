package extension.undertow.session;

import java.io.Reader;
import java.lang.reflect.Type;

/**
 * The {@code JsonSerializer} interface.
 */
public interface JsonSerializer {
    /**
     * Returns the result of {@code toJson}.
     *
     * @param o the o
     * @return the result
     */
    String toJson(Object o);
    /**
     * Returns the result of {@code fromJson}.
     *
     * @param <T> the type parameter
     * @param json the json
     * @param clazz the clazz
     * @return the result
     */
    <T> T fromJson(String json, Class<T> clazz);
    /**
     * Returns the result of {@code fromJson}.
     *
     * @param <T> the type parameter
     * @param json the json
     * @param type the type
     * @return the result
     */
    <T> T fromJson(String json, Type type);
    /**
     * Returns the result of {@code fromJson}.
     *
     * @param <T> the type parameter
     * @param json the json
     * @param clazz the clazz
     * @return the result
     */
    <T> T fromJson(Reader json, Class<T> clazz);
}
