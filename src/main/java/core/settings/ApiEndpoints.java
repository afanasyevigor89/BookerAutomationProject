package core.settings;

import org.apache.http.auth.AUTH;

public enum ApiEndpoints {
    PING("/ping"),
    BOOKING("/booking"),
    AUTH("/auth");


    private final String path;

    ApiEndpoints(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
