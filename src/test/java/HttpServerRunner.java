import java.io.IOException;

class HttpServerRunner {
    private final Server server;

    HttpServerRunner(int port) {
        this.server = Server.builder()
                .port(port)
                .build();

        addIntegrationTestRoutes();
    }

    protected void run() {
        Thread.ofVirtual().start(() -> {
            try {
                this.server.run();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected void shutdown() {
        this.server.shutdown();
    }

    protected int getPort() {
        return this.server.getPort();
    }

    private void addIntegrationTestRoutes() {
        server.addRoute(Http.Method.GET, "/integration/plaintext", request -> Response.builder()
                .status(Http.Status.OK)
                .body("response from integration/plaintext")
                .build()
        );
    }


}
