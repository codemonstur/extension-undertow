package extension.undertow.server;

import extension.undertow.error.InvalidInput;
import extension.undertow.model.Header;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.util.HeaderMap;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Deque;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

import static io.undertow.util.Headers.X_FORWARDED_FOR;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.isRegularFile;
import static java.util.stream.Collectors.joining;

public enum RequestParser {;

    private static final FormParserFactory parsers = FormParserFactory
        .builder().withDefaultCharset(UTF_8.name()).build();
    public static FormData parseForm(final HttpServerExchange exchange) throws IOException, InvalidInput {
        final var parser = requireNotNull(parsers.createParser(exchange), "HTTP request did not contain a valid form");
        exchange.startBlocking();
        return parser.parseBlocking();
    }
    public static FormData parseForm(final HttpServerExchange exchange, final Set<String> parameters)
            throws InvalidInput, IOException {
        final var form = parseForm(exchange);
        for (final var input : form) {
            if (!parameters.contains(input))
                throw new InvalidInput(format("Unknown form parameter '%s', valid parameters are: %s", input, parameters));
        }
        return form;
    }
    public static Function<String, String> firstValueFrom(final FormData form) {
        return key -> {
            final var value = form.getFirst(key);
            return value == null ? null : value.getValue();
        };
    }
    public static <T extends Enum<T>> T getMandatoryEnum(final String queryString, final Class<T> enumClass, final String parameter) throws InvalidInput {
        return toEnumValue(getMandatoryString(queryString, parameter), enumClass);
    }
    public static long getMandatoryLong(final String queryString, final String parameter) throws InvalidInput {
        try { return Long.parseLong(getMandatoryString(queryString, parameter)); }
        catch (final NumberFormatException e) {
            throw new InvalidInput(format("Parameter %s must contain a long", parameter));
        }
    }
    public static String getMandatoryString(final String queryString, final String parameter) throws InvalidInput {
        if (!isNullOrEmpty(queryString)) {
            final String qParameter = parameter+"=";
            final String[] parts = queryString.split("&");
            for (final String param : parts) {
                if (param.startsWith(qParameter)) {
                    return decodeUrl(param.substring(qParameter.length()));
                }
            }
        }
        throw new InvalidInput(format("Missing parameter '%s'", parameter));
    }

    public static <T extends Enum<T>> T getMandatoryEnum(final FormData formData, final Class<T> enumClass, final String parameter) throws InvalidInput {
        final var value = formData.getFirst(parameter);
        if (value == null) throw new InvalidInput(format("Missing parameter '%s'", parameter));
        return toEnumValue(value.getValue(), enumClass);
    }

    public static String getMandatoryEmailAddress(final FormData formData, final String parameter) throws InvalidInput {
        final var value = formData.getFirst(parameter);
        if (value == null) throw new InvalidInput(format("Missing parameter '%s'", parameter));
        return requireEmailAddress(value.getValue(), "Parameter '" + parameter + "' is not a valid email address").toLowerCase();
    }

    public static String getMandatoryString(final FormData formData, final String parameter) throws InvalidInput {
        final var value = formData.getFirst(parameter);
        if (value == null) throw new InvalidInput(format("Missing parameter '%s'", parameter));
        return value.getValue();
    }
    public static int getMandatoryInteger(final FormData formData, final String parameter) throws InvalidInput {
        try { return Integer.parseInt(getMandatoryString(formData, parameter)); }
        catch (final NumberFormatException e) {
            throw new InvalidInput(format("Parameter '%s' must contain an integer", parameter));
        }
    }
    public static long getMandatoryLong(final FormData formData, final String parameter) throws InvalidInput {
        try { return Long.parseLong(getMandatoryString(formData, parameter)); }
        catch (final NumberFormatException e) {
            throw new InvalidInput(format("Parameter '%s' must contain a long", parameter));
        }
    }
    public static boolean getMandatoryBoolean(final FormData formData, final String parameter) throws InvalidInput {
        final String value = getMandatoryString(formData, parameter);
        if ("true".equalsIgnoreCase(value)) return true;
        if ("false".equalsIgnoreCase(value)) return false;
        throw new InvalidInput(format("Parameter '%s' must contain 'true' or 'false'", parameter));
    }
    public static double getMandatoryDouble(final FormData formData, final String parameter) throws InvalidInput {
        try { return Double.parseDouble(getMandatoryString(formData, parameter)); }
        catch (final NumberFormatException e) {
            throw new InvalidInput(format("Parameter '%s' must contain a double", parameter));
        }
    }

    public static <T extends Enum<T>> T getOptionalEnum(final FormData formData, final Class<T> enumClass
            , final String parameter, final T defaultValue) throws InvalidInput {
        final var value = formData.getFirst(parameter);
        return value == null ? defaultValue : toEnumValue(value.getValue(), enumClass);
    }

    public static String getOptionalString(final FormData formData, final String parameter, final String defaultValue) {
        final var value = formData.getFirst(parameter);
        return (value != null && value.getValue() != null && !value.getValue().isEmpty()) ? value.getValue() : defaultValue;
    }
    public static Integer getOptionalInteger(final FormData formData, final String parameter, final Integer defaultValue) throws InvalidInput {
        final var value = formData.getFirst(parameter);
        if (value == null || value.getValue() == null || value.getValue().isEmpty()) return defaultValue;

        try { return Integer.parseInt(value.getValue()); }
        catch (final NumberFormatException e) {
            throw new InvalidInput(format("Parameter '%s' must contain an integer", parameter));
        }
    }
    public static Long getOptionalLong(final FormData formData, final String parameter, final Long defaultValue) throws InvalidInput {
        final var value = formData.getFirst(parameter);
        if (value == null || value.getValue() == null || value.getValue().isEmpty()) return defaultValue;

        try { return Long.parseLong(value.getValue()); }
        catch (final NumberFormatException e) {
            throw new InvalidInput(format("Parameter '%s' must contain an integer", parameter));
        }
    }
    public static boolean getOptionalBoolean(final FormData formData, final String parameter, final boolean defaultValue) throws InvalidInput {
        final var value = formData.getFirst(parameter);
        if (value == null || value.getValue() == null || value.getValue().isEmpty()) return defaultValue;

        if ("true".equalsIgnoreCase(value.getValue())) return true;
        if ("false".equalsIgnoreCase(value.getValue())) return false;
        throw new InvalidInput(format("Parameter '%s' must contain 'true' or 'false'", parameter));
    }
    public static Double getOptionalDouble(final FormData formData, final String parameter, final Double defaultValue) throws InvalidInput {
        final var value = formData.getFirst(parameter);
        if (value == null || value.getValue() == null || value.getValue().isEmpty()) return defaultValue;

        try { return Double.parseDouble(value.getValue()); }
        catch (final NumberFormatException e) {
            throw new InvalidInput(format("Parameter '%s' must contain a double", parameter));
        }
    }

    public static String getOptionalString(final HttpServerExchange exchange, final String parameter, final String defaultValue) {
        final var params = exchange.getQueryParameters().get(parameter);
        return (params == null || params.isEmpty()) ? defaultValue : params.getFirst();
    }
    public static int getBoundedInteger(final HttpServerExchange exchange, final String parameter, final int minimum, final int defaultValue, final int maximum) throws InvalidInput {
        return rangeBound(minimum, maximum, getOptionalInteger(exchange, parameter, defaultValue));
    }
    public static Integer getOptionalInteger(final HttpServerExchange exchange, final String parameter, final Integer defaultValue) throws InvalidInput {
        final var params = exchange.getQueryParameters().get(parameter);
        if (params == null || params.isEmpty()) return defaultValue;

        try { return Integer.parseInt(params.getFirst()); }
        catch (final NumberFormatException e) {
            throw new InvalidInput(format("Parameter '%s' must contain an integer", parameter));
        }
    }

    public static long getBoundedLong(final HttpServerExchange exchange, final String parameter, final long minimum, final long defaultValue, final long maximum) throws InvalidInput {
        return rangeBound(minimum, maximum, getOptionalLong(exchange, parameter, defaultValue));
    }
    public static Long getOptionalLong(final HttpServerExchange exchange, final String parameter, final Long defaultValue) throws InvalidInput {
        final var params = exchange.getQueryParameters().get(parameter);
        if (params == null || params.isEmpty()) return defaultValue;

        try { return Long.parseLong(params.getFirst()); }
        catch (final NumberFormatException e) {
            throw new InvalidInput(format("Parameter '%s' must contain a long", parameter));
        }
    }
    public static Boolean getOptionalBoolean(final HttpServerExchange exchange, final String parameter, final Boolean defaultValue) throws InvalidInput {
        final var params = exchange.getQueryParameters().get(parameter);
        if (params == null || params.isEmpty()) return defaultValue;

        if ("true".equalsIgnoreCase(params.getFirst())) return true;
        if ("false".equalsIgnoreCase(params.getFirst())) return false;
        throw new InvalidInput(format("Parameter '%s' must contain 'true' or 'false'", parameter));
    }
    public static Double getOptionalDouble(final HttpServerExchange exchange, final String parameter, final Double defaultValue) throws InvalidInput {
        final Deque<String> params = exchange.getQueryParameters().get(parameter);
        if (params == null || params.isEmpty()) return defaultValue;

        try { return Double.parseDouble(params.getFirst()); }
        catch (final NumberFormatException e) {
            throw new InvalidInput(format("Parameter '%s' must contain a double", parameter));
        }
    }

    public static <T extends Enum<T>> T getMandatoryEnum(final HttpServerExchange exchange, final Class<T> enumClass, final Header header) throws InvalidInput {
        final Deque<String> params = exchange.getRequestHeaders().get(header.toString());
        if (params == null || params.isEmpty()) throw new InvalidInput(format("Missing header '%s'", header));
        return toEnumValue(params.getFirst(), enumClass);
    }
    public static String getMandatoryString(final HttpServerExchange exchange, final Header header) throws InvalidInput {
        final Deque<String> params = exchange.getRequestHeaders().get(header.toString());
        if (params == null || params.isEmpty()) throw new InvalidInput(format("Missing header '%s'", header));
        return params.getFirst();
    }
    public static String getOptionalString(final HttpServerExchange exchange, final Header header, final String defaultValue) {
        final Deque<String> params = exchange.getRequestHeaders().get(header.toString());
        return params == null || params.isEmpty() ? defaultValue : params.getFirst();
    }

    public static <T extends Enum<T>> T getMandatoryEnum(final HttpServerExchange exchange, final Class<T> enumClass, final String parameter) throws InvalidInput {
        final Deque<String> params = exchange.getQueryParameters().get(parameter);
        if (params == null || params.isEmpty()) throw new InvalidInput(format("Missing parameter '%s'", parameter));
        return toEnumValue(params.getFirst(), enumClass);
    }
    public static String getMandatoryString(final HttpServerExchange exchange, final String parameter) throws InvalidInput {
        final Deque<String> params = exchange.getQueryParameters().get(parameter);
        if (params == null || params.isEmpty()) throw new InvalidInput(format("Missing parameter '%s'", parameter));
        return params.getFirst();
    }
    public static Path getMandatoryFile(final HttpServerExchange exchange, final String parameter) throws InvalidInput {
        final Deque<String> params = exchange.getQueryParameters().get(parameter);
        if (params == null || params.isEmpty()) throw new InvalidInput(format("Parameter '%s' does not point to a file", parameter));
        final Path file = Paths.get(params.getFirst());
        if (!isRegularFile(file)) throw new InvalidInput(format("Parameter '%s' does not point to a file", parameter));
        return file;
    }

    public static int getMandatoryInteger(final HttpServerExchange exchange, final String parameter) throws InvalidInput {
        final Deque<String> params = exchange.getQueryParameters().get(parameter);
        if (params == null || params.isEmpty()) throw new InvalidInput(format("Missing parameter '%s'", parameter));

        try { return Integer.parseInt(params.getFirst()); }
        catch (final NumberFormatException e) {
            throw new InvalidInput(format("Parameter '%s' must contain an integer", parameter));
        }
    }
    public static boolean getMandatoryBoolean(final HttpServerExchange exchange, final String parameter) throws InvalidInput {
        final String value = getMandatoryString(exchange, parameter);
        if ("true".equalsIgnoreCase(value)) return true;
        if ("false".equalsIgnoreCase(value)) return false;
        throw new InvalidInput(format("Parameter '%s' must contain 'true' or 'false'", parameter));
    }

    public static double getMandatoryDouble(final HttpServerExchange exchange, final String parameter) throws InvalidInput {
        final Deque<String> params = exchange.getQueryParameters().get(parameter);
        if (params == null || params.isEmpty()) throw new InvalidInput(format("Missing parameter '%s'", parameter));

        try { return Double.parseDouble(params.getFirst()); }
        catch (final NumberFormatException e) {
            throw new InvalidInput(format("Parameter '%s' must contain a double", parameter));
        }
    }
    public static long getMandatoryLong(final HttpServerExchange exchange, final String parameter) throws InvalidInput {
        final Deque<String> params = exchange.getQueryParameters().get(parameter);
        if (params == null || params.isEmpty()) throw new InvalidInput(format("Missing parameter '%s'", parameter));

        try { return Long.parseLong(params.getFirst()); }
        catch (final NumberFormatException e) {
            throw new InvalidInput(format("Parameter '%s' must contain a long", parameter));
        }
    }

    public static String getMandatoryString(final HeaderMap headers, final String header) throws InvalidInput {
        final String headerValue = headers.getFirst(header);
        if (headerValue == null) throw new InvalidInput(format("Missing header '%s'", header));
        return headerValue;
    }
    public static String getOptionalString(final HeaderMap headers, final String header, final String defaultValue) {
        final String headerValue = headers.getFirst(header);
        return (headerValue != null) ? headerValue : defaultValue;
    }

    public static String toBasicAuthHeader(final String username, final String password) {
        return "Basic " + toBasicAuthCredentials(username, password);
    }
    public static String toBasicAuthCredentials(final String username, final String password) {
        return encodeBase64(username + ':' + password, UTF_8);
    }

    public static String getSourceIPAddress(final HttpServerExchange exchange) {
        final String xForwardedFor = exchange.getRequestHeaders().getFirst(X_FORWARDED_FOR);
        if (!isNullOrEmpty(xForwardedFor)) {
            final String[] addresses = xForwardedFor.split(",");
            if (addresses.length != 0 && !isNullOrEmpty(addresses[0]) && isValidOrigin(addresses[0])) {
                return addresses[0];
            }
        }
        return getRequestingIP(exchange);
    }

    public static String getRequestingIP(final HttpServerExchange exchange) {
        return exchange.getSourceAddress().getAddress().toString().substring(1);
    }

    public static String getRequestBody(final HttpServerExchange exchange, final Charset charset) throws IOException {
        return new String(exchange.getInputStream().readAllBytes(), charset);
    }

    private static final Pattern IP_ADDRESS = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+");
    private static boolean isValidOrigin(final String origin) {
        return IP_ADDRESS.matcher(origin).matches();
    }

    public static boolean parseBoolean(final String parameter, final String value) throws InvalidInput {
        if ("true".equalsIgnoreCase(value)) return true;
        if ("false".equalsIgnoreCase(value)) return false;
        throw new InvalidInput(format("Parameter '%s' must contain 'true' or 'false'", parameter));
    }
    public static long parseLong(final String parameter, final String value) throws InvalidInput {
        try {
            return Long.parseLong(value);
        } catch (final NumberFormatException e) {
            throw new InvalidInput(format("Parameter '%s' must contain a long", parameter));
        }
    }
    public static int parseInt(final String parameter, final String value) throws InvalidInput {
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            throw new InvalidInput(format("Parameter '%s' must contain an integer", parameter));
        }
    }

    public static void restrictQueryParameters(final HttpServerExchange exchange, final String... parameters) throws InvalidInput {
        final var valid = Set.of(parameters);
        for (final var input : exchange.getQueryParameters().keySet()) {
            if (!valid.contains(input)) throw new InvalidInput(format("Unknown query parameter '%s'", input));
        }
    }

    private static <T extends Enum<T>> T toEnumValue(final String value, final Class<T> enumClass) throws InvalidInput {
        try {
            return Enum.valueOf(enumClass, value);
        } catch (final Exception e) {
            final String names = Arrays
                .stream(enumClass.getEnumConstants())
                .map(Object::toString)
                .collect(joining(", "));
            throw new InvalidInput(format("Invalid enum '%s', possible values are: %s", value, names));
        }
    }

}
