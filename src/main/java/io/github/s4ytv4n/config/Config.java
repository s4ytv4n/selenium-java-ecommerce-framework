package io.github.s4ytv4n.config;

import io.github.s4ytv4n.driver.Browser;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

/**
 * Runtime settings. Lookup order for every key: JVM property, environment variable
 * (upper case, dots replaced by underscores), config.properties.
 */
public final class Config {

    private static final Properties FILE = load();

    private Config() {
    }

    public static String baseUrl() {
        return trimSlash(get("base.url"));
    }

    public static String apiUrl() {
        return trimSlash(get("api.url"));
    }

    public static Browser browser() {
        return Browser.from(get("browser"));
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("headless"));
    }

    public static Duration explicitWait() {
        return Duration.ofSeconds(Long.parseLong(get("explicit.wait.seconds")));
    }

    public static Duration pageLoadTimeout() {
        return Duration.ofSeconds(Long.parseLong(get("page.load.timeout.seconds")));
    }

    private static String get(String key) {
        String value = System.getProperty(key);
        if (isBlank(value)) {
            value = System.getenv(key.toUpperCase().replace('.', '_'));
        }
        if (isBlank(value)) {
            value = FILE.getProperty(key);
        }
        if (isBlank(value)) {
            throw new IllegalStateException("Missing configuration value: " + key);
        }
        return value.trim();
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties not found on the classpath");
            }
            properties.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read config.properties", e);
        }
        return properties;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String trimSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
