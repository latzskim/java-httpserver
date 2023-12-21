import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;

public class Response {
    private final Http.Status status;
    private final byte[] body;

    public Response(Http.Status status) {
        this.status = status;
        this.body = null;
    }

    public Response(ResponseBuilder b) {
        this.status = b.status;
        this.body = b.body;
    }

    public static ResponseBuilder builder() {
        return new ResponseBuilder();
    }

    public void send(OutputStream outputStream) {
        PrintWriter out = new PrintWriter(outputStream, false);

        out.printf("HTTP/1.1 %d %s\r\n", this.status.getCode(), this.status);
        out.printf("Content-Type: text/plain; charset=UTF-8\r\n");
        out.printf("\r\n");
        if (this.body != null) {
            out.write(new String(this.body));
        }
        out.flush();
    }

    public static class ResponseBuilder {
        private Http.Status status;

        private byte[] body;


        public ResponseBuilder status(Http.Status status) {
            this.status = status;
            return this;
        }

        public ResponseBuilder body(byte[] body) {
            this.body = body;
            return this;
        }

        public Response build() {
            return new Response(this);
        }
    }
}
