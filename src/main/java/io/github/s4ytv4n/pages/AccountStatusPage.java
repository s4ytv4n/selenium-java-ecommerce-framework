package io.github.s4ytv4n.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

/** Confirmation screens shown after creating or deleting an account. */
public class AccountStatusPage extends BasePage {

    private static final By CREATED = By.cssSelector("[data-qa='account-created']");
    private static final By DELETED = By.cssSelector("[data-qa='account-deleted']");
    private static final By CONTINUE = By.cssSelector("[data-qa='continue-button']");

    public String accountCreatedTitle() {
        return text(CREATED);
    }

    public String accountDeletedTitle() {
        return text(DELETED);
    }

    @Step("Continue")
    public HomePage continueToHome() {
        click(CONTINUE);
        return new HomePage();
    }
}
