import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        var server = Server.builder()
                .port(8080)
                .build();

        server.addRoute(Http.Method.GET, "/tests", (req) -> Response.builder()
                .status(Http.Status.OK)
                .body("Hello from tests, anonymous user")
                .build());

        server.addRoute(Http.Method.GET, "/tests/object",request -> Response.builder()
                .status(Http.Status.OK)
                .body(new TestDTO())
                .build()
        );

        server.addRoute(Http.Method.GET, "/tests/{id}", request -> Response.builder()
                .status(Http.Status.OK)
                .body("Hello from tests, user id: " + request.getParam("id"))
                .build());

        server.run();
    }
}