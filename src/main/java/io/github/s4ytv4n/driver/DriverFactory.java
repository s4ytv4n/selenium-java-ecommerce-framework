package io.github.s4ytv4n.driver;

import io.github.s4ytv4n.config.Config;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/** Creates configured browser sessions. Drivers are resolved by Selenium Manager, no WebDriverManager needed. */
public final class DriverFactory {

    private static final Logger log = LoggerFactory.getLogger(DriverFactory.class);
    private static final Dimension WINDOW = new Dimension(1440, 900);

    /**
     * Ad, analytics and consent-banner hosts. They add nothing to functional checks but slow pages
     * and inject overlays that intercept clicks - the main source of flakiness on the demo store.
     */
    private static final List<String> BLOCKED_URLS = List.of(
            "*googlesyndication.com*", "*doubleclick.net*", "*googleadservices.com*",
            "*adservice.google*", "*google-analytics.com*", "*googletagmanager.com*",
            "*fundingchoicesmessages.google.com*", "*amazon-adsystem.com*");

    private DriverFactory() {
    }

    public static WebDriver create(Browser browser, boolean headless) {
        log.info("Starting {} (headless: {})", browser, headless);
        WebDriver driver = switch (browser) {
            case CHROME -> new ChromeDriver(chromeOptions(headless));
            case EDGE -> new EdgeDriver(edgeOptions(headless));
            case FIREFOX -> new FirefoxDriver(firefoxOptions(headless));
        };
        driver.manage().window().setSize(WINDOW);
        driver.manage().timeouts().pageLoadTimeout(Config.pageLoadTimeout());
        if (driver instanceof ChromiumDriver chromium) {
            blockThirdPartyNoise(chromium);
        }
        return driver;
    }

    private static ChromeOptions chromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.addArguments("--disable-dev-shm-usage", "--no-sandbox", "--disable-search-engine-choice-screen");
        if (headless) {
            options.addArguments("--headless=new");
        }
        return options;
    }

    private static EdgeOptions edgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.addArguments("--disable-dev-shm-usage");
        if (headless) {
            options.addArguments("--headless=new");
        }
        return options;
    }

    private static FirefoxOptions firefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        if (headless) {
            options.addArguments("-headless");
        }
        return options;
    }

    /** Chrome DevTools Protocol: drop requests to ad/tracking hosts before they reach the network. */
    private static void blockThirdPartyNoise(ChromiumDriver driver) {
        driver.executeCdpCommand("Network.enable", Map.of());
        driver.executeCdpCommand("Network.setBlockedURLs", Map.<String, Object>of("urls", BLOCKED_URLS));
    }
}
