public class DefaultHandlers {
    public static final RequestHandler NOT_FOUND_HANDLER =
            request -> new Response(Http.Status.NOT_FOUND);
}
