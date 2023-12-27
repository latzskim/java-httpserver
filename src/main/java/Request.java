import com.sun.net.httpserver.Headers;

import java.io.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    private final Http.Method method;
    private final URI uri;
    private final Query query;
    private final Headers headers;
    private final InputStream bodyReader;

    public Request(Http.Method method,
                   String uri,
                   Map<String, List<String>> headers,
                   InputStream bodyReader) throws URISyntaxException {
        this.method = method;
        this.uri = new URI(uri);
        this.query = new Query(this.uri);
        this.headers = new Headers(headers);
        this.bodyReader = bodyReader;
    }

    public static Request from(InputStream input) throws IOException, URISyntaxException {
        // Parse request metadata:
        StringBuilder meta = new StringBuilder();
        while (true) {
            var readByte = input.read();
            if (readByte <= 0) {
                break;
            }

            if ((char) readByte == '\n') {
                break;
            }

            meta.append((char) readByte);
        }


        var requestMeta = meta.toString().split(" ");
        var method = Http.Method.valueOf(requestMeta[0]);
        var uri = requestMeta[1];
        var receivedVersion = requestMeta[2];

        if (!receivedVersion.trim().equals(Http.VERSION_1_1)) {
            throw new UnsupportedHttpVersionException("Unsupported HTTP request version: " + receivedVersion);
        }

        // Parse headers:
        var headerBytes = new byte[Http.MAX_HEADER_LENGTH];
        int headerLen = 0, lastByte = 0, lineLen = 0;
        while (true) {
            var readByte = input.read();
            if (readByte <= 0) {
                break;
            }

            headerLen++;
            if (headerLen >= Http.MAX_HEADER_LENGTH) {
                throw new RemoteException("header too large"); // TODO: better error handling
            }

            // End of headers:
            lineLen++;
            if (readByte == '\n' && lastByte == '\r') {
                if (lineLen == 2) {
                    headerBytes[headerLen - 1] = (byte) readByte;
                    break;
                }
                lineLen = 0;
            }

            headerBytes[headerLen - 1] = (byte) readByte;
            lastByte = readByte;
        }


        var headers = new HashMap<String, List<String>>();
        new BufferedReader(new StringReader(new String(headerBytes)))
                .lines()
                .map((l) -> l.split(":"))
                .filter((l) -> l.length == 2)
                .forEach((arrL) -> {
                    var values = Arrays.stream(arrL[1].split(";"))
                            .map(String::trim)
                            .toList();
                    headers.put(arrL[0], values);
                });

        return new Request(method, uri, headers, input);
    }

    public Http.Method getMethod() {
        return this.method;
    }

    public String getPath() {
        return this.uri.getPath();
    }

    public boolean keepAlive() {
        return false;
    }

    public Query getQuery() {
        return this.query;
    }

    public InputStream bodyStream() {
        return this.bodyReader;
    }

    public Headers getHeaders() {
        return this.headers;
    }

    public Reader body() throws IOException {
        var len = Integer.parseInt(this.headers.getFirst("Content-Length"));
        var str = new String(bodyReader.readNBytes(len));
        return new StringReader(str);
    }
}


