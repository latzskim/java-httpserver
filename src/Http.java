public class Http {
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
