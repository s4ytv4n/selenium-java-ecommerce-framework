package io.github.s4ytv4n.pages;

import io.qameta.allure.Step;

public class HomePage extends BasePage {

    @Step("Open home page")
    public HomePage open() {
        openPath("/");
        return this;
    }
}
