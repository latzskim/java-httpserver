
import java.io.Reader;

public interface BodySerializer<T> {
    T deserialize(Reader r, Class<T> clazz) throws DeserializeException;

    String serialize(T clazz) throws SerializeException;
}
