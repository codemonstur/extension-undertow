package extension.undertow.server;

import common.error.InvalidInput;
import common.error.NotFound;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static common.error.ErrorCodes.*;
import static common.util.Security.calculateHaystackSize;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;

public enum Validator {;

    public static <T> T requireTrue(final boolean valid, final T value, final String message) throws InvalidInput {
        if (!valid) throw new InvalidInput(message);
        return value;
    }

    public static String requireAlpha(final String input, final String message) throws InvalidInput {
        if (!isAlpha(input)) throw new InvalidInput(message);
        return input;
    }

    public static <T> T requireNotNull(final T input, final String message) throws InvalidInput {
        if (input == null) throw new InvalidInput(message);
        return input;
    }

    public static String requireNotEmpty(final String input, final String message) throws InvalidInput {
        if (input == null || input.isEmpty()) throw new InvalidInput(message);
        return input;
    }
    public static <T> T[] requireNotEmpty(final T[] array, final String message) throws InvalidInput {
        if (array == null || array.length == 0) throw new InvalidInput(message);
        return array;
    }
    public static byte[] requireNotEmpty(final byte[] array, final String message) throws InvalidInput {
        if (array == null || array.length == 0) throw new InvalidInput(message);
        return array;
    }

    private static final Pattern valid_email = Pattern.compile("[a-z0-9\\-_+\\.]+@[a-z0-9-\\.]+");
    public static String requireEmailAddress(final String email, final String message) throws InvalidInput {
        if (email == null || !valid_email.matcher(email).matches())
            throw new InvalidInput(invalid_email_address, message);
        return email;
    }

    public static <T> T requireMatch(final Pattern pattern, final T value, final String message) throws InvalidInput {
        if (value == null || !pattern.matcher(value.toString()).matches()) throw new InvalidInput(message);
        return value;
    }

    public static <T> T requireMatch(final String pattern, final T value, final String message) throws InvalidInput {
        return requireMatch(Pattern.compile(pattern), value, message);
    }

    public static <T> T requireInSet(final Set<?> set, final T value, final String message) throws InvalidInput {
        if (!set.contains(value)) throw new InvalidInput(message);
        return value;
    }

    public static File requireDirectory(final String dirname) throws InvalidInput {
        return requireDirectory(dirname, format("The selected directory (%s) is not available", dirname));
    }

    public static File requireDirectory(final String dirname, final String message) throws InvalidInput {
        final File dir = new File(dirname);
        if ((!dir.exists() && !dir.mkdirs()) || (dir.exists() && !dir.isDirectory()))
            throw new InvalidInput(message);
        return dir;
    }

    public static String requireImage(final String contentType, final String message) throws InvalidInput {
        if (isNullOrEmpty(contentType) || !contentType.startsWith("image/")) throw new InvalidInput(not_an_image, message);
        return contentType;
    }

    public static int requirePortNumber(final String port, final String message) throws InvalidInput {
        if (port == null || port.isEmpty()) throw new InvalidInput(message);
        try {
            return Integer.parseInt(port);
        } catch (final Exception e) {
            throw new InvalidInput(message);
        }
    }

    public static boolean isNullOrEmpty(final String string) {
        return string == null || string.isEmpty();
    }
    public static boolean isNullOrEmpty(final List<?> list) {
        return list == null || list.isEmpty();
    }
    public static boolean isNullOrEmpty(final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isIndexOf(final List<?> list, final int index) {
        return index >= 0 && index < list.size();
    }

    public static <T> T orDefault(final T value, final T defaultValue) {
        return (value != null) ? value : defaultValue;
    }
    public static Integer orDefault(final Integer value, final Integer defaultValue) {
        return (value != null) ? value : defaultValue;
    }
    public static Long orDefault(final Long value, final Long defaultValue) {
        return (value != null) ? value : defaultValue;
    }
    public static Boolean orDefault(final Boolean value, final Boolean defaultValue) {
        return (value != null) ? value : defaultValue;
    }

    public static int orDefault(final String value, final int defaultValue) {
        if (value == null || value.isEmpty()) return defaultValue;
        return Integer.parseInt(value);
    }
    public static boolean orDefault(final String value, final boolean defaultValue) {
        if (value == null || value.isEmpty()) return defaultValue;
        return Boolean.parseBoolean(value);
    }
    public static long orDefault(final String value, final long defaultValue) {
        if (value == null || value.isEmpty()) return defaultValue;
        return Long.parseLong(value);
    }
    public static String orDefault(final String value, final String defaultValue) {
        if (value == null || value.isEmpty()) return defaultValue;
        return value;
    }
    public static <V, T extends Throwable> V orThrow(final V value, final Supplier<T> exception) throws T {
        if (value == null) throw exception.get();
        return value;
    }

    public static int rangeBound(final int minimum, final int maximum, final int value) {
        if (value < minimum) return minimum;
        if (value > maximum) return maximum;
        return value;
    }
    public static long rangeBound(final long minimum, final long maximum, final long value) {
        if (value < minimum) return minimum;
        if (value > maximum) return maximum;
        return value;
    }

    public static <T> T isFound(final T value) throws NotFound {
        if (value != null) return value;
        throw new NotFound();
    }

    private static final Pattern NUMERIC = Pattern.compile("-?[0-9]+");
    private static final Pattern ALPHA = Pattern.compile("[a-zA-Z]+");
    public static boolean isNumeric(final String input) {
        return NUMERIC.matcher(input).matches();
    }
    public static boolean isAlpha(final String input) {
        return ALPHA.matcher(input).matches();
    }

    public static boolean areNull(final Object... values) {
        for (final Object v : values)
            if (v != null)
                return false;
        return true;
    }

    public static long isAfter(final long moment, final long age, final String message) throws InvalidInput {
        if (moment < currentTimeMillis() - age) throw new InvalidInput(message);
        return moment;
    }
    public static long isEarlierThan(final long before, final long after) throws InvalidInput {
        if (before > after) throw new InvalidInput(format("Before value %d is after value %d", before, after));
        return before;
    }

    public static boolean isImage(final String contentType) {
        return !isNullOrEmpty(contentType) && contentType.startsWith("image/");
    }

    public static String requireStrongPassword(final String password) throws InvalidInput {
        if (calculateHaystackSize(password) < 35)
            throw new InvalidInput(password_not_strong_enough, "Password is not strong enough");
        return password;
    }

}
