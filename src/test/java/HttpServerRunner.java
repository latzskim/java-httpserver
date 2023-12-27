import java.io.IOException;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;

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

        server.addRoute(Http.Method.GET, "/integration/json", request -> Response.builder()
                .status(Http.Status.OK)
                .body(new TestDTO())
                .build()
        );

        server.addRoute(Http.Method.GET, "/integration/query", request -> {
            var query = request.getQuery();
            String str = query.find("str", String.class).get();
            Character c = query.find("char", Character.class).get();
            Integer i = query.find("int", Integer.class).get();
            Boolean b = query.find("bool", Boolean.class).get();
            Float f = query.find("float", Float.class).get();
            Double d = query.find("float", Double.class).get();
            Integer[] arr = query.find("array", Integer[].class).get();
            String arrRaw = query.raw("array");

            DecimalFormat df2 = new DecimalFormat("#.##");
            df2.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));

            var body = String.format(
                    "str:%s,char:%s,int:%d,bool:%b,float:%s,double:%s,arr:%s,arrRaw:%s",
                    str, c, i, b, df2.format(f), df2.format(d), Arrays.toString(arr), arrRaw
            );

            return Response.builder()
                    .status(Http.Status.OK)
                    .body(body)
                    .build();
        });

        server.addRoute(Http.Method.GET, "/integration/throwexception", request -> {
            throw new RuntimeException("always exception");
        });

        server.addRoute(Http.Method.POST, "/integration/echo", request -> {
            var headers = request.getHeaders();
            var bodyLength = Integer.parseInt(headers.getFirst("Content-Length"));
            var bodyReader = request.bodyStream();

            // TODO: add limit for body len?
            // Also handle chunks and chunk-size;

            var buf = new byte[bodyLength];
            if (bodyReader.read(buf) == -1) {
                throw new RemoteException("expected body to be not empty");
            }

            return Response.builder()
                    .status(Http.Status.OK)
                    .body(new String(buf))
                    .build();
        });

    }


}
