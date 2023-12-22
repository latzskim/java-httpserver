import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Request {
    private final Http.Method method;
    private final String path;
    private final Map<String, List<String>> headers;

    public static Request from(InputStream input) throws IOException {
        var buffReader = new BufferedReader(new InputStreamReader(input));

        var requestMeta = buffReader.readLine().split(" ");
        var method = Http.Method.valueOf(requestMeta[0]);
        var path = requestMeta[1];
        var receivedVersion = requestMeta[2];

        if (!receivedVersion.trim().equals(Http.VERSION_1_1)) {
            throw new UnsupportedHttpVersionException("Unsupported HTTP request version: " + receivedVersion);
        }

        var headers = new HashMap<String, List<String>>();
        var line = "";
        while (!(line = buffReader.readLine()).isBlank()) {
            var headerMeta = line.split(":");
            var values = Arrays
                    .stream(headerMeta[1].split(";"))
                    .map(String::trim)
                    .collect(Collectors.toList());

            headers.put(headerMeta[0].trim(), values);
        }

        return new Request(method, path, headers);
    }

    public Request(Http.Method method, String path, Map<String, List<String>> headers) {
        this.method = method;
        this.path = path;
        this.headers = headers;
    }

    public Http.Method getMethod() {
        return this.method;
    }

    public String getPath() {
        return this.path;
    }

    public boolean keepAlive() {
        return false;
    }

    public <T> T getParam(String param) {
        return null;
    }
}
