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
        assertEquals(200, response.statusCode());

        assertEquals(
                "text/plain; charset=UTF-8",
                response.headers().firstValue("Content-Type").orElse("")
        );
        assertEquals("response from integration/plaintext", response.body());
    }

    @Test
    public void shouldReturnJsonBody() throws IOException, InterruptedException {
        // given:
        var uri = String.format("http://localhost:%s/integration/json", runner.getPort());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Content-Type", "text/plain")
                .GET()
                .build();

        // when:
        HttpResponse<String> response = c.send(request, HttpResponse.BodyHandlers.ofString());


        // then:
        assertEquals(200, response.statusCode());

        assertEquals(
                "application/json; charset=UTF-8",
                response.headers().firstValue("Content-Type").orElse("")
        );
        assertEquals("""
                        {"i":10,"i2":11,"f":12.1,"f2":13.2,"d":14.3,"d2":15.4,"s":"TestStr","c":"T","c2":"2","testEnum":"E2","p":{"x":1,"y":2}}""",
                response.body());
    }

    @Test
    public void shouldReturn404() throws IOException, InterruptedException {
        var uri = String.format("http://localhost:%s/notfound", runner.getPort());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Content-Type", "text/plain")
                .GET()
                .build();

        // when:
        HttpResponse<Void> response = c.send(request, HttpResponse.BodyHandlers.discarding());

        // then:
        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldReturn500() throws IOException, InterruptedException {
        var uri = String.format("http://localhost:%s/integration/throwexception", runner.getPort());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Content-Type", "text/plain")
                .GET()
                .build();

        // when:
        HttpResponse<Void> response = c.send(request, HttpResponse.BodyHandlers.discarding());

        // then:
        assertEquals(500, response.statusCode());
    }

    @Test
    public void shouldParseQuery() throws IOException, InterruptedException {
        // given:
        var query = "&str=CAPITAL" +
                "&char=T" +
                "&int=1" +
                "&bool=true" +
                "&float=3.14" +
                "&array=1&array=2&array=3";

        var uri = String.format("http://localhost:%s/integration/query?%s", runner.getPort(), query);


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Content-Type", "text/plain")
                .GET()
                .build();

        // when:
        HttpResponse<String> response = c.send(request, HttpResponse.BodyHandlers.ofString());


        // then:
        assertEquals(200, response.statusCode());

        assertEquals(
                "text/plain; charset=UTF-8",
                response.headers().firstValue("Content-Type").orElse("")
        );
        assertEquals(
                "str:CAPITAL,char:T,int:1,bool:true,float:3.14,double:3.14,arr:[1, 2, 3],arrRaw:array=1&array=2&array=3",
                response.body());
    }

    @Test
    public void shouldReturnPOSTBody() throws IOException, InterruptedException {
        var uri = String.format("http://localhost:%s/integration/echo", runner.getPort());

        var bodyJson = "{\"test\": \"abc\", \"arr\":[1,2,3]}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                .build();

        // when:
        HttpResponse<String> response = c.send(request, HttpResponse.BodyHandlers.ofString());

        // then:
        assertEquals(200, response.statusCode());
        assertEquals(bodyJson, response.body());
    }
}