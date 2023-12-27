import java.lang.reflect.Array;
import java.net.URI;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Query {
    private final URI uri;
    private Map<String, List<String>> params;
    private final Map<Class<?>, Function<String, ?>> typeConverters;

    public Query(URI uri) {
        this.uri = uri;
        this.params = new HashMap<>();
        this.typeConverters = new HashMap<>();

        registerConverters();
        parseQuery();
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> find(String paramName, Class<T> clazz) {
        var param = this.params.get(paramName);
        if (param == null) {
            return Optional.empty();
        }

        if (clazz.isArray()) {
            Function<String, ?> converter = typeConverters.get(clazz.getComponentType());
            var array = createGenericArray(clazz.getComponentType(), param.size());
            for (int i = 0; i < param.size(); i++) {
                array[i] = converter.apply(param.get(i));
            }

            return Optional.of((T) array);
        }

        if (param.size() > 1) {
            var msg = "Requested value is not an array, but multiple elements found for query parameter: %s";
            throw new AmbiguousElementException(String.format(msg, paramName));
        }

        var singleParam = param.getFirst();

        Function<String, ?> converter = typeConverters.get(clazz);
        if (converter != null) {
            return Optional.ofNullable((T) converter.apply(singleParam));
        }

        throw new IllegalArgumentException("Unsupported query parameter type: " + clazz.getName());
    }

    public String raw(String paramName) {
        var param = this.params.get(paramName);
        if (param == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < param.size(); i++) {
            sb.append(paramName).append("=").append(param.get(i));
            if (i < param.size() - 1) {
                sb.append('&');
            }
        }

        return sb.toString();
    }

    private void parseQuery() {
        if (this.uri.getQuery() == null) {
            return;
        }

        var keysValues = this.uri.getQuery().split("&");
        this.params = Arrays.stream(keysValues)
                .map((kv) -> kv.split("="))
                .filter((kv) -> kv.length == 2)
                .collect(Collectors.groupingBy(entry -> entry[0], Collectors.mapping(entry -> entry[1], Collectors.toList())));
    }

    private void registerConverters() {
        typeConverters.put(String.class, Function.identity());
        typeConverters.put(Character.class, s -> s.isBlank() ? null : s.charAt(0));
        typeConverters.put(Integer.class, s -> s.isBlank() ? null : Integer.parseInt(s));
        typeConverters.put(Float.class, s -> s.isBlank() ? null : Float.parseFloat(s));
        typeConverters.put(Double.class, s -> s.isBlank() ? null : Double.parseDouble(s));
        typeConverters.put(Boolean.class, Boolean::parseBoolean);
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] createGenericArray(Class<T> elementType, int size) {
        return (T[]) Array.newInstance(elementType, size);
    }
}
