package core.settings;

public enum ApiEndpoints {
    PING("/ping"),
    BOOKING("/booking"),
    BOOKINGID("/booking/141");

    private final String path;

    ApiEndpoints(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
