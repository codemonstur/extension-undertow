package extension.undertow.server;

import extension.undertow.error.InvalidInput;
import extension.undertow.error.NotFound;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;

/**
 * The {@code Validator} enum.
 */
public enum Validator {;

    /**
     * Returns the result of {@code requireTrue}.
     *
     * @param <T> the type parameter
     * @param valid the valid
     * @param value the value
     * @param message the message
     * @return the result
     * @throws InvalidInput if the operation fails
     */
    public static <T> T requireTrue(final boolean valid, final T value, final String message) throws InvalidInput {
        if (!valid) throw new InvalidInput(message);
        return value;
    }

    /**
     * Returns the result of {@code requireAlpha}.
     *
     * @param input the input
     * @param message the message
     * @return the result
     * @throws InvalidInput if the operation fails
     */
    public static String requireAlpha(final String input, final String message) throws InvalidInput {
        if (!isAlpha(input)) throw new InvalidInput(message);
        return input;
    }

    /**
     * Returns the result of {@code requireNotNull}.
     *
     * @param <T> the type parameter
     * @param input the input
     * @param message the message
     * @return the result
     * @throws InvalidInput if the operation fails
     */
    public static <T> T requireNotNull(final T input, final String message) throws InvalidInput {
        if (input == null) throw new InvalidInput(message);
        return input;
    }

    /**
     * Returns the result of {@code requireNotEmpty}.
     *
     * @param input the input
     * @param message the message
     * @return the result
     * @throws InvalidInput if the operation fails
     */
    public static String requireNotEmpty(final String input, final String message) throws InvalidInput {
        if (input == null || input.isEmpty()) throw new InvalidInput(message);
        return input;
    }
    /**
     * Returns the result of {@code requireNotEmpty}.
     *
     * @param <T> the type parameter
     * @param array the array
     * @param message the message
     * @return the result
     * @throws InvalidInput if the operation fails
     */
    public static <T> T[] requireNotEmpty(final T[] array, final String message) throws InvalidInput {
        if (array == null || array.length == 0) throw new InvalidInput(message);
        return array;
    }
    /**
     * Returns the result of {@code requireNotEmpty}.
     *
     * @param array the array
     * @param message the message
     * @return the result
     * @throws InvalidInput if the operation fails
     */
    public static byte[] requireNotEmpty(final byte[] array, final String message) throws InvalidInput {
        if (array == null || array.length == 0) throw new InvalidInput(message);
        return array;
    }

    private static final Pattern valid_email = Pattern.compile("[a-z0-9\\-_+\\.]+@[a-z0-9-\\.]+");
    /**
     * Returns the result of {@code requireEmailAddress}.
     *
     * @param email the email
     * @param message the message
     * @return the result
     * @throws InvalidInput if the operation fails
     */
    public static String requireEmailAddress(final String email, final String message) throws InvalidInput {
        if (email == null || !valid_email.matcher(email).matches())
            throw new InvalidInput(message);
        return email;
    }

    /**
     * Returns the result of {@code requireMatch}.
     *
     * @param <T> the type parameter
     * @param pattern the pattern
     * @param value the value
     * @param message the message
     * @return the result
     * @throws InvalidInput if the operation fails
     */
    public static <T> T requireMatch(final Pattern pattern, final T value, final String message) throws InvalidInput {
        if (value == null || !pattern.matcher(value.toString()).matches()) throw new InvalidInput(message);
        return value;
    }

    /**
     * Returns the result of {@code requireMatch}.
     *
     * @param <T> the type parameter
     * @param pattern the pattern
     * @param value the value
     * @param message the message
     * @return the result
     * @throws InvalidInput if the operation fails
     */
    public static <T> T requireMatch(final String pattern, final T value, final String message) throws InvalidInput {
        return requireMatch(Pattern.compile(pattern), value, message);
    }

    /**
     * Returns the result of {@code requireInSet}.
     *
     * @param <T> the type parameter
     * @param set the set
     * @param value the value
     * @param message the message
     * @return the result
     * @throws InvalidInput if the operation fails
     */
    public static <T> T requireInSet(final Set<?> set, final T value, final String message) throws InvalidInput {
        if (!set.contains(value)) throw new InvalidInput(message);
        return value;
    }

    /**
     * Returns the result of {@code requireDirectory}.
     *
     * @param dirname the dirname
     * @return the result
     * @throws InvalidInput if the operation fails
     */
    public static File requireDirectory(final String dirname) throws InvalidInput {
        return requireDirectory(dirname, format("The selected directory (%s) is not available", dirname));
    }

    /**
     * Returns the result of {@code requireDirectory}.
     *
     * @param dirname the dirname
     * @param message the message
     * @return the result
     * @throws InvalidInput if the operation fails
     */
    public static File requireDirectory(final String dirname, final String message) throws InvalidInput {
        final File dir = new File(dirname);
        if ((!dir.exists() && !dir.mkdirs()) || (dir.exists() && !dir.isDirectory()))
            throw new InvalidInput(message);
        return dir;
    }

    /**
     * Returns the result of {@code requireImage}.
     *
     * @param contentType the content type
     * @param message the message
     * @return the result
     * @throws InvalidInput if the operation fails
     */
    public static String requireImage(final String contentType, final String message) throws InvalidInput {
        if (isNullOrEmpty(contentType) || !contentType.startsWith("image/")) throw new InvalidInput(message);
        return contentType;
    }

    /**
     * Returns the result of {@code requirePortNumber}.
     *
     * @param port the port
     * @param message the message
     * @return the result
     * @throws InvalidInput if the operation fails
     */
    public static int requirePortNumber(final String port, final String message) throws InvalidInput {
        if (port == null || port.isEmpty()) throw new InvalidInput(message);
        try {
            return Integer.parseInt(port);
        } catch (final Exception e) {
            throw new InvalidInput(message);
        }
    }

    /**
     * Returns the result of {@code isNullOrEmpty}.
     *
     * @param string the string
     * @return whether the condition holds
     */
    public static boolean isNullOrEmpty(final String string) {
        return string == null || string.isEmpty();
    }
    /**
     * Returns the result of {@code isNullOrEmpty}.
     *
     * @param list the list
     * @return whether the condition holds
     */
    public static boolean isNullOrEmpty(final List<?> list) {
        return list == null || list.isEmpty();
    }
    /**
     * Returns the result of {@code isNullOrEmpty}.
     *
     * @param map the map
     * @return whether the condition holds
     */
    public static boolean isNullOrEmpty(final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * Returns the result of {@code isIndexOf}.
     *
     * @param list the list
     * @param index the index
     * @return whether the condition holds
     */
    public static boolean isIndexOf(final List<?> list, final int index) {
        return index >= 0 && index < list.size();
    }

    /**
     * Returns the result of {@code orDefault}.
     *
     * @param <T> the type parameter
     * @param value the value
     * @param defaultValue the default value
     * @return the result
     */
    public static <T> T orDefault(final T value, final T defaultValue) {
        return (value != null) ? value : defaultValue;
    }
    /**
     * Returns the result of {@code orDefault}.
     *
     * @param value the value
     * @param defaultValue the default value
     * @return the result
     */
    public static Integer orDefault(final Integer value, final Integer defaultValue) {
        return (value != null) ? value : defaultValue;
    }
    /**
     * Returns the result of {@code orDefault}.
     *
     * @param value the value
     * @param defaultValue the default value
     * @return the result
     */
    public static Long orDefault(final Long value, final Long defaultValue) {
        return (value != null) ? value : defaultValue;
    }
    /**
     * Returns the result of {@code orDefault}.
     *
     * @param value the value
     * @param defaultValue the default value
     * @return whether the condition holds
     */
    public static Boolean orDefault(final Boolean value, final Boolean defaultValue) {
        return (value != null) ? value : defaultValue;
    }

    /**
     * Returns the result of {@code orDefault}.
     *
     * @param value the value
     * @param defaultValue the default value
     * @return the result
     */
    public static int orDefault(final String value, final int defaultValue) {
        if (value == null || value.isEmpty()) return defaultValue;
        return Integer.parseInt(value);
    }
    /**
     * Returns the result of {@code orDefault}.
     *
     * @param value the value
     * @param defaultValue the default value
     * @return whether the condition holds
     */
    public static boolean orDefault(final String value, final boolean defaultValue) {
        if (value == null || value.isEmpty()) return defaultValue;
        return Boolean.parseBoolean(value);
    }
    /**
     * Returns the result of {@code orDefault}.
     *
     * @param value the value
     * @param defaultValue the default value
     * @return the result
     */
    public static long orDefault(final String value, final long defaultValue) {
        if (value == null || value.isEmpty()) return defaultValue;
        return Long.parseLong(value);
    }
    /**
     * Returns the result of {@code orDefault}.
     *
     * @param value the value
     * @param defaultValue the default value
     * @return the result
     */
    public static String orDefault(final String value, final String defaultValue) {
        if (value == null || value.isEmpty()) return defaultValue;
        return value;
    }
    /**
     * Returns the result of {@code orThrow}.
     *
     * @param <V> the type parameter
     * @param <T> the type parameter
     * @param value the value
     * @param exception the exception
     * @return the result
     * @throws T if the operation fails
     */
    public static <V, T extends Throwable> V orThrow(final V value, final Supplier<T> exception) throws T {
        if (value == null) throw exception.get();
        return value;
    }

    /**
     * Returns the result of {@code rangeBound}.
     *
     * @param minimum the minimum
     * @param maximum the maximum
     * @param value the value
     * @return the result
     */
    public static int rangeBound(final int minimum, final int maximum, final int value) {
        if (value < minimum) return minimum;
        if (value > maximum) return maximum;
        return value;
    }
    /**
     * Returns the result of {@code rangeBound}.
     *
     * @param minimum the minimum
     * @param maximum the maximum
     * @param value the value
     * @return the result
     */
    public static long rangeBound(final long minimum, final long maximum, final long value) {
        if (value < minimum) return minimum;
        if (value > maximum) return maximum;
        return value;
    }

    /**
     * Returns the result of {@code isFound}.
     *
     * @param <T> the type parameter
     * @param value the value
     * @return the result
     * @throws NotFound if the operation fails
     */
    public static <T> T isFound(final T value) throws NotFound {
        if (value != null) return value;
        throw new NotFound();
    }

    private static final Pattern NUMERIC = Pattern.compile("-?[0-9]+");
    private static final Pattern ALPHA = Pattern.compile("[a-zA-Z]+");
    /**
     * Returns the result of {@code isNumeric}.
     *
     * @param input the input
     * @return whether the condition holds
     */
    public static boolean isNumeric(final String input) {
        return NUMERIC.matcher(input).matches();
    }
    /**
     * Returns the result of {@code isAlpha}.
     *
     * @param input the input
     * @return whether the condition holds
     */
    public static boolean isAlpha(final String input) {
        return ALPHA.matcher(input).matches();
    }

    /**
     * Returns the result of {@code areNull}.
     *
     * @param values the values
     * @return whether the condition holds
     */
    public static boolean areNull(final Object... values) {
        for (final Object v : values)
            if (v != null)
                return false;
        return true;
    }

    /**
     * Returns the result of {@code isAfter}.
     *
     * @param moment the moment
     * @param age the age
     * @param message the message
     * @return the result
     * @throws InvalidInput if the operation fails
     */
    public static long isAfter(final long moment, final long age, final String message) throws InvalidInput {
        if (moment < currentTimeMillis() - age) throw new InvalidInput(message);
        return moment;
    }
    /**
     * Returns the result of {@code isEarlierThan}.
     *
     * @param before the before
     * @param after the after
     * @return the result
     * @throws InvalidInput if the operation fails
     */
    public static long isEarlierThan(final long before, final long after) throws InvalidInput {
        if (before > after) throw new InvalidInput(format("Before value %d is after value %d", before, after));
        return before;
    }

    /**
     * Returns the result of {@code isImage}.
     *
     * @param contentType the content type
     * @return whether the condition holds
     */
    public static boolean isImage(final String contentType) {
        return !isNullOrEmpty(contentType) && contentType.startsWith("image/");
    }

    /**
     * Returns the result of {@code requireStrongPassword}.
     *
     * @param password the password
     * @return the result
     * @throws InvalidInput if the operation fails
     */
    public static String requireStrongPassword(final String password) throws InvalidInput {
        if (password == null || password.length() < 8)
            throw new InvalidInput("Password is not strong enough");
        return password;
    }

}
