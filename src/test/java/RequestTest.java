import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class RequestTest {

    @Test
    void shouldThrowUnsupportedHttpVersionException() {
        // given
        var in = new ByteArrayInputStream("GET /tests HTTP/1.0".getBytes());

        // when & then:
        assertThrows(
                UnsupportedHttpVersionException.class,
                () -> Request.from(in)
        );
    }
}