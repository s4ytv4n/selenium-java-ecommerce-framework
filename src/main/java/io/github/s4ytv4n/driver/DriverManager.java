package io.github.s4ytv4n.driver;

import io.github.s4ytv4n.config.Config;
import org.openqa.selenium.WebDriver;

/** One browser per test thread, so TestNG can run methods in parallel safely. */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void start() {
        DRIVER.set(DriverFactory.create(Config.browser(), Config.headless()));
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("No browser session for this thread. Did the test extend BaseUiTest?");
        }
        return driver;
    }

    public static boolean isStarted() {
        return DRIVER.get() != null;
    }

    public static void quit() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                DRIVER.remove();
            }
        }
    }
}
