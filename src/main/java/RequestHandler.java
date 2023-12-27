import java.io.IOException;

@FunctionalInterface
public interface RequestHandler<T> {
    Response<T> handle(Request request) throws IOException, DeserializeException;
}
