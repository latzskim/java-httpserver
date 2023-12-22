public class Response<T> {
    private final Http.Status status;
    private final T body;
    private final String contentType; // TODO: enum?

    public Response(Http.Status status) {
        this.status = status;
        this.contentType = "text/plain";
        this.body = null;
    }

    public Response(ResponseBuilder<T> b) {
        this.status = b.status;
        this.body = b.body;
        this.contentType = b.contentType;
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

    public String getContentType() {
        return this.contentType;
    }

    public static class ResponseBuilder<T> {
        private String contentType;
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

        public ResponseBuilder<T> contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Response<T> build() {
            return new Response<>(this);
        }
    }
}
