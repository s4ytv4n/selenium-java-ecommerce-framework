package io.github.s4ytv4n.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

/** /login hosts two forms side by side: existing-customer login and new-customer signup. */
public class LoginPage extends BasePage {

    private static final By LOGIN_EMAIL = By.cssSelector("[data-qa='login-email']");
    private static final By LOGIN_PASSWORD = By.cssSelector("[data-qa='login-password']");
    private static final By LOGIN_BUTTON = By.cssSelector("[data-qa='login-button']");
    private static final By LOGIN_ERROR = By.cssSelector("form[action='/login'] p");

    private static final By SIGNUP_NAME = By.cssSelector("[data-qa='signup-name']");
    private static final By SIGNUP_EMAIL = By.cssSelector("[data-qa='signup-email']");
    private static final By SIGNUP_BUTTON = By.cssSelector("[data-qa='signup-button']");
    private static final By SIGNUP_ERROR = By.cssSelector("form[action='/signup'] p");

    @Step("Open login page")
    public LoginPage open() {
        openPath("/login");
        return this;
    }

    @Step("Log in as {email}")
    public HomePage login(String email, String password) {
        submitLogin(email, password);
        return new HomePage();
    }

    @Step("Try to log in as {email}")
    public LoginPage loginExpectingError(String email, String password) {
        submitLogin(email, password);
        return this;
    }

    @Step("Start signup for {name} / {email}")
    public SignupPage startSignup(String name, String email) {
        fillSignup(name, email);
        return new SignupPage();
    }

    @Step("Try to start signup with {email}")
    public LoginPage startSignupExpectingError(String name, String email) {
        fillSignup(name, email);
        return this;
    }

    @Step("Submit an empty login form")
    public LoginPage submitEmptyLogin() {
        click(LOGIN_BUTTON);
        return this;
    }

    public String loginError() {
        return text(LOGIN_ERROR);
    }

    public String signupError() {
        return text(SIGNUP_ERROR);
    }

    /** True when the browser blocks submission because a required field is empty (HTML5 validation). */
    public boolean isLoginEmailMissing() {
        return (Boolean) js("return arguments[0].validity.valueMissing;", visible(LOGIN_EMAIL));
    }

    public boolean isLoginPasswordMissing() {
        return (Boolean) js("return arguments[0].validity.valueMissing;", visible(LOGIN_PASSWORD));
    }

    private void submitLogin(String email, String password) {
        type(LOGIN_EMAIL, email);
        type(LOGIN_PASSWORD, password);
        click(LOGIN_BUTTON);
    }

    private void fillSignup(String name, String email) {
        type(SIGNUP_NAME, name);
        type(SIGNUP_EMAIL, email);
        click(SIGNUP_BUTTON);
    }
}
