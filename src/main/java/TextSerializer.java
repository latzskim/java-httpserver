import java.io.BufferedReader;
import java.io.Reader;

public class TextSerializer implements BodySerializer<String> {
    @Override
    public String deserialize(Reader r, Class<String> clazz) throws DeserializeException {
        try {
            var bf = new BufferedReader(r);
            var sb = new StringBuilder();

            String line;
            while (!(line = bf.readLine()).isBlank()) {
                sb.append(line);
            }

            return sb.toString();
        } catch (Exception e) {
            throw new DeserializeException();
        }
    }

    @Override
    public String serialize(String obj) throws SerializeException {
        return obj;
    }
}
