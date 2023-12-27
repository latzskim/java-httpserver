import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class BodySerializers {
    private static BodySerializers instance;
    private final Map<Class<?>, BodySerializer<?>> serializers;
    private BodySerializer<?> defaultSerializer;

    private BodySerializers() {
        this.serializers = new HashMap<>();
    }

    public static synchronized BodySerializers getInstance() {
        if (instance == null) {
            instance = new BodySerializers();
        }
        return instance;
    }

    public <T> void registerSerializer(Class<T> clazz, BodySerializer<T> serializer) {
        this.serializers.put(clazz, serializer);
    }

    public void setDefaultSerializer(BodySerializer<?> defaultSerializer) {
        if (this.defaultSerializer == null) {
            this.defaultSerializer = defaultSerializer;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialize(Reader r, Class<T> clazz) throws DeserializeException {
        BodySerializer<T> serializer = (BodySerializer<T>) this.serializers.get(clazz);
        if (serializer == null) {
            serializer = (BodySerializer<T>) this.defaultSerializer;
        }

        return serializer.deserialize(r, clazz);
    }

    @SuppressWarnings("unchecked")
    public <T> String serialize(T obj) throws SerializeException {
        BodySerializer<T> serializer = (BodySerializer<T>) this.serializers.get(obj.getClass());
        if (serializer == null) {
            serializer = (BodySerializer<T>) this.defaultSerializer;
        }

        return serializer.serialize(obj);
    }

}
