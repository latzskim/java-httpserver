import com.google.gson.Gson;

import java.io.Reader;

public class GsonDefaultSerializer<T> implements BodySerializer<T> {

    // TODO: use GsonBuilder
    private final Gson gson;

    public GsonDefaultSerializer() {
        this.gson = new Gson();
    }

    @Override
    public T deserialize(Reader r, Class<T> clazz) throws DeserializeException {
        try {
            return gson.fromJson(r, clazz);
        } catch (Exception e) {
            throw new DeserializeException();
        }
    }

    @Override
    public String serialize(T obj) throws SerializeException {
        try {
            return gson.toJson(obj);
        } catch (Exception e) {
            throw new SerializeException();
        }
    }
}
