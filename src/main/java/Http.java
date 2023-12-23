public class Http {
    public static final String VERSION_1_1 = "HTTP/1.1";
    public static final int MAX_HEADER_LENGTH = 1024 * 8;

    public enum Method {
        GET
    }

    public enum Status {

        // 2xx
        OK(200),

        // 4xx
        NOT_FOUND(404),
        NOT_ALLOWED(405),

        // 5xx
        INTERNAL_SERVER_ERROR(500),

        SERVICE_UNAVAILABLE(503);

        private final int value;

        private Status(int value) {
            this.value = value;
        }

        public int getCode() {
            return this.value;
        }
    }
}
