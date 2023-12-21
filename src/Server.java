import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

enum State {
    READY,
    RUNNING,
    SHUTTING_DOWN,
}

public class Server {
    private int port;
    private final Map<Http.Method, Map<String, RequestHandler>> handlers;

    private State state;

    private Server(ServerBuilder serverBuilder) {
        this.port = serverBuilder.port;
        this.state = State.READY;

        this.handlers = new HashMap<>();
        Arrays.stream(Http.Method.values())
                .forEach((httpMethod) -> handlers.put(httpMethod, new HashMap<>()));
    }

    public static ServerBuilder builder() {
        return new ServerBuilder();
    }

    public void run() throws IOException {
        this.state = State.RUNNING;


        try (ServerSocket socket = new ServerSocket(this.port)) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                // TODO: add logs
                System.out.println("graceful shutdown");
                this.state = State.SHUTTING_DOWN;

                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));

            while (this.state == State.RUNNING) {
                var sock = socket.accept();

                Thread.ofVirtual().name(sock.toString())
                        .start(() -> tryHandleConnection(sock));
            }
        }
    }

    private void tryHandleConnection(Socket sock) {
        try {
            this.handleConnection(sock);
        } catch (Exception e) {
            // TODO: log?
        } finally {
            // TODO: finally block will be removed to handle keep-alive?
            try {
                sock.close();
            } catch (IOException e) {
                this.state = State.SHUTTING_DOWN;
            }
        }
    }

    private void handleConnection(Socket sock) throws IOException {
        var req = Request.from(sock);

        var methodHandlers = this.handlers.get(req.getMethod());
        if (methodHandlers == null || methodHandlers.isEmpty()) {
            var response = Response.builder()
                    .status(Http.Status.NOT_ALLOWED)
                    .build();

            response.send(sock.getOutputStream());
            return;
        }

        var response = methodHandlers
                .getOrDefault(req.getPath(), DefaultHandlers.NOT_FOUND_HANDLER)
                .handle(req);

        response.send(sock.getOutputStream());
    }

    // TODO: take the last handler or throw exception.
    public void addRoute(Http.Method httpMethod, String path, RequestHandler handler) {
        this.handlers.computeIfPresent(httpMethod, ((method, handlers) -> {
            handlers.putIfAbsent(path, handler);
            return handlers;
        }));
    }

    public static class ServerBuilder {
        private int port;

        public ServerBuilder port(int port) {
            this.port = port;
            return this;
        }

        public Server build() {
            return new Server(this);
        }
    }
}


