import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
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
    private final Map<Http.Method, Map<String, RequestHandler<?>>> handlers;
    private final Map<java.lang.Class<?>, BodySerializer<?>> serializers;

    private final BodySerializer<?> defaultSerializer;

    private State state;

    private Server(ServerBuilder serverBuilder) {
        this.port = serverBuilder.port;
        this.defaultSerializer = serverBuilder.defaultSerializer;

        this.state = State.READY;

        this.handlers = new HashMap<>();
        Arrays.stream(Http.Method.values())
                .forEach((httpMethod) -> handlers.put(httpMethod, new HashMap<>()));

        this.serializers = new HashMap<>();
        this.serializers.put(String.class, new TextSerializer());
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

    public void shutdown() {
        this.state = State.SHUTTING_DOWN;
    }

    private void tryHandleConnection(Socket sock) {
        try {
            this.handleConnection(sock);
        } catch (Exception e) {
            // TODO: log?
            e.printStackTrace();
        } finally {
            // TODO: finally block will be removed to handle keep-alive?
            try {
                sock.close();
            } catch (IOException e) {
                this.state = State.SHUTTING_DOWN;
            }
        }
    }

    private void handleConnection(Socket sock) throws IOException, SerializeException {
        var req = Request.from(sock.getInputStream());

        var methodHandlers = this.handlers.get(req.getMethod());
        if (methodHandlers == null || methodHandlers.isEmpty()) {
            var response = Response.builder()
                    .status(Http.Status.NOT_ALLOWED)
                    .build();

            handleResponse(response, sock.getOutputStream());
            return;
        }

        var response = methodHandlers
                .getOrDefault(req.getPath(), DefaultHandlers.NOT_FOUND_HANDLER)
                .handle(req);

        handleResponse(response, sock.getOutputStream());
    }

    @SuppressWarnings("unchecked")
    private <T> void handleResponse(Response<T> response, OutputStream outputStream) throws SerializeException {
        PrintWriter w = new PrintWriter(outputStream);

        var contentType = getContentType(response);

        w.printf("HTTP/1.1 %d %s\r\n", response.getStatus().getCode(), response.getStatus());
        w.printf("Content-Type: %s; charset=UTF-8\r\n", contentType);

        var body = response.getBody();
        if (body == null) {
            w.flush();
            return;
        }

        w.printf("\r\n");

        BodySerializer<T> serializer = (BodySerializer<T>) this.serializers.get(body.getClass());
        if (serializer != null) {
            w.write(serializer.serialize(body));
            w.flush();
            return;
        }

        w.write(((BodySerializer<T>) this.defaultSerializer).serialize(body));
        w.flush();
    }

    private <T> String getContentType(Response<T> response) {
        if (response.getContentType() != null && !response.getContentType().isBlank()) {
            return response.getContentType();
        }

        var body = response.getBody();
        if (body == null) {
            return "text/plain";
        }

        return switch (body) {
            case String ignored -> "text/plain";
            case Character ignored -> "text/plain";
            default -> "application/json";
        };
    }

    // TODO: take the last handler or throw exception.
    public void addRoute(Http.Method httpMethod, String path, RequestHandler<?> handler) {
        this.handlers.computeIfPresent(httpMethod, ((method, handlers) -> {
            handlers.putIfAbsent(path, handler);
            return handlers;
        }));
    }

    public int getPort() {
        return this.port;
    }


    public static class ServerBuilder {
        private int port;
        private BodySerializer<?> defaultSerializer;

        public ServerBuilder port(int port) {
            this.port = port;
            return this;
        }

        public ServerBuilder defaultSerializer(BodySerializer<?> defaultSerializer) {
            this.defaultSerializer = defaultSerializer;
            return this;
        }

        public Server build() {
            if (this.defaultSerializer == null) {
                this.defaultSerializer = new GsonDefaultSerializer<>();
            }

            return new Server(this);
        }
    }
}


