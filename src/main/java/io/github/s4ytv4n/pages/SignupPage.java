package io.github.s4ytv4n.pages;

import io.github.s4ytv4n.models.User;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/** Second signup step: "Enter Account Information". */
public class SignupPage extends BasePage {

    private static final By NAME = qa("name");
    private static final By EMAIL = qa("email");
    private static final By CREATE_ACCOUNT = qa("create-account");

    private static By qa(String value) {
        return By.cssSelector("[data-qa='" + value + "']");
    }

    public String prefilledName() {
        return visible(NAME).getDomProperty("value");
    }

    public String prefilledEmail() {
        return visible(EMAIL).getDomProperty("value");
    }

    @Step("Fill account information")
    public SignupPage fillAccountInformation(User user) {
        click(By.id("Mr".equals(user.title()) ? "id_gender1" : "id_gender2"));
        type(qa("password"), user.password());
        selectByValue(qa("days"), user.birthDay());
        selectByValue(qa("months"), user.birthMonth());
        selectByValue(qa("years"), user.birthYear());

        type(qa("first_name"), user.firstName());
        type(qa("last_name"), user.lastName());
        type(qa("company"), user.company());
        type(qa("address"), user.address1());
        type(qa("address2"), user.address2());
        selectByText(qa("country"), user.country());
        type(qa("state"), user.state());
        type(qa("city"), user.city());
        type(qa("zipcode"), user.zipcode());
        type(qa("mobile_number"), user.mobileNumber());
        return this;
    }

    @Step("Create account")
    public AccountStatusPage createAccount() {
        click(CREATE_ACCOUNT);
        return new AccountStatusPage();
    }
}
