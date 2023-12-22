public class Response<T> {
    private final Http.Status status;
    private final T body;

    public Response(Http.Status status) {
        this.status = status;
        this.body = null;
    }

    public Response(ResponseBuilder<T> b) {
        this.status = b.status;
        this.body = b.body;
    }


    public static <T> ResponseBuilder<T> builder() {
        return new ResponseBuilder<>();
    }

    public T getBody() {
        return this.body;
    }

    public Http.Status getStatus() {
        return this.status;
    }

    public static class ResponseBuilder<T> {
        private Http.Status status;

        private T body;


        public ResponseBuilder<T> status(Http.Status status) {
            this.status = status;
            return this;
        }

        public ResponseBuilder<T> body(T body) {
            this.body = body;
            return this;
        }

        public Response<T> build() {
            return new Response<>(this);
        }
    }
}
