import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        var server = Server.builder()
                .port(8080)
                .build();

        server.addRoute(Http.Method.GET, "/test", (req) -> Response.builder()
                .status(Http.Status.OK)
                .body("TestResponseFromRoute:)".getBytes())
                .build());
        server.run();
    }
}