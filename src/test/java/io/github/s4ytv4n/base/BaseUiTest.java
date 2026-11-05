package io.github.s4ytv4n.base;

import io.github.s4ytv4n.api.ApiResponse;
import io.github.s4ytv4n.api.StoreApiClient;
import io.github.s4ytv4n.data.UserFactory;
import io.github.s4ytv4n.driver.DriverManager;
import io.github.s4ytv4n.models.User;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Starts a fresh browser per test method and tears everything down afterwards:
 * failure evidence goes to Allure, test accounts created through the API are deleted.
 */
public abstract class BaseUiTest {

    protected static final Logger log = LoggerFactory.getLogger(BaseUiTest.class);

    protected final StoreApiClient api = new StoreApiClient();

    // Methods may run in parallel on one class instance, so per-test state lives in ThreadLocals.
    private final ThreadLocal<List<User>> createdUsers = ThreadLocal.withInitial(ArrayList::new);

    @BeforeMethod(alwaysRun = true)
    public void startBrowser() {
        DriverManager.start();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        try {
            if (!result.isSuccess() && DriverManager.isStarted()) {
                attachFailureEvidence(DriverManager.getDriver());
            }
        } finally {
            DriverManager.quit();
            deleteCreatedUsers();
        }
    }

    /** Creates an account through the API (milliseconds instead of a UI signup) and schedules its deletion. */
    protected User registeredUser() {
        User user = UserFactory.create();
        ApiResponse created = api.createAccount(user);
        if (created.responseCode() != 201) {
            throw new IllegalStateException("Precondition failed, createAccount said: " + created.message());
        }
        createdUsers.get().add(user);
        return user;
    }

    /** Registers a user created by the test itself (e.g. via UI signup) for cleanup. */
    protected void cleanUpAfterTest(User user) {
        createdUsers.get().add(user);
    }

    private void deleteCreatedUsers() {
        for (User user : createdUsers.get()) {
            try {
                api.deleteAccount(user.email(), user.password());
            } catch (RuntimeException e) {
                log.warn("Could not delete test account {}: {}", user.email(), e.getMessage());
            }
        }
        createdUsers.remove();
    }

    private static void attachFailureEvidence(WebDriver driver) {
        try {
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Screenshot", "image/png", new ByteArrayInputStream(png), "png");
            Allure.addAttachment("Page URL", driver.getCurrentUrl());
            Allure.addAttachment("Page source", "text/html",
                    new ByteArrayInputStream(driver.getPageSource().getBytes(StandardCharsets.UTF_8)), "html");
        } catch (RuntimeException e) {
            log.warn("Could not capture failure evidence: {}", e.getMessage());
        }
    }
}
