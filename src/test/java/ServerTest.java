import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServerTest {
    private HttpServerRunner runner;
    private HttpClient c;

    @BeforeAll
    public void setup() {
        c = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        runner = new HttpServerRunner(7777);
        runner.run();
    }

    @AfterAll
    public void cleanup() {
        runner.shutdown();
    }

    @Test
    public void shouldReturnTextPlainBody() throws IOException, InterruptedException {
        // given:
        var uri = String.format("http://localhost:%s/integration/plaintext", runner.getPort());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Content-Type", "text/plain")
                .GET()
                .build();

        // when:
        HttpResponse<String> response = c.send(request, HttpResponse.BodyHandlers.ofString());

        // then:
        assertEquals(
                "text/plain; charset=UTF-8",
                response.headers().firstValue("Content-Type").orElse("")
        );
        assertEquals("response from integration/plaintext", response.body());
    }

}