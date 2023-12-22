
@FunctionalInterface
public interface RequestHandler<T> {
    Response<T> handle(Request request);
}
