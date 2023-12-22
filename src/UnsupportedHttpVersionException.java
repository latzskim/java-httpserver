public class UnsupportedHttpVersionException extends RuntimeException {
    private final String message;

    public UnsupportedHttpVersionException(String msg) {
        this.message = msg;
    }

    @Override
    public String toString() {
        return message;
    }
}
