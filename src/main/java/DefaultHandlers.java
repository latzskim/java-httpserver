public class DefaultHandlers {
    public static final RequestHandler<?> NOT_FOUND_HANDLER =
            request -> new Response<>(Http.Status.NOT_FOUND);

    public static final RequestHandler<?> INTERNAL_SERVER_ERROR_HANDLER =
            request -> new Response<>(Http.Status.INTERNAL_SERVER_ERROR);
}
