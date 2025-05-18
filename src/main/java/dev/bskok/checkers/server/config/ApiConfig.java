package dev.bskok.checkers.server.config;

import java.io.IOException;
import java.util.Properties;

public class ApiConfig {
    private static final Properties properties = new Properties();

    static {
        try {
            properties.load(ApiConfig.class.getResourceAsStream("/api.properties"));
        } catch (IOException e) {
            // Fallback to defaults
            properties.setProperty("api.baseUrl", "http://localhost:8080/api");
        }
    }

    public static String getBaseUrl() {
        return properties.getProperty("api.baseUrl");
    }

    public static void setBaseUrl(String url) {
        properties.setProperty("api.baseUrl", url);
    }
}
