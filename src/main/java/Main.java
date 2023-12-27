import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        var server = Server.builder()
                .port(8080)
                .build();

        server.run();
    }
}