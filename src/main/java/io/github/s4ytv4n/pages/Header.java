package io.github.s4ytv4n.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class Header extends BasePage {

    private static final By NAV = By.cssSelector("#header .shop-menu");
    private static final By PRODUCTS = By.cssSelector("#header .shop-menu a[href='/products']");
    private static final By CART = By.cssSelector("#header .shop-menu a[href='/view_cart']");
    private static final By SIGNUP_LOGIN = By.cssSelector("#header .shop-menu a[href='/login']");
    private static final By LOGOUT = By.cssSelector("#header .shop-menu a[href='/logout']");
    private static final By DELETE_ACCOUNT = By.cssSelector("#header .shop-menu a[href='/delete_account']");
    private static final By LOGGED_IN_AS = By.xpath("//div[contains(@class,'shop-menu')]//li[contains(., 'Logged in as')]");

    public boolean isLoggedIn() {
        visible(NAV);
        return isVisible(LOGGED_IN_AS);
    }

    public String loggedInUserName() {
        return text(LOGGED_IN_AS).replace("Logged in as", "").trim();
    }

    public boolean isSignupLoginLinkVisible() {
        return isVisible(SIGNUP_LOGIN);
    }

    @Step("Go to Products")
    public ProductsPage goToProducts() {
        click(PRODUCTS);
        return new ProductsPage();
    }

    @Step("Go to Cart")
    public CartPage goToCart() {
        click(CART);
        return new CartPage();
    }

    @Step("Log out")
    public LoginPage logout() {
        click(LOGOUT);
        return new LoginPage();
    }

    @Step("Delete account from the header")
    public AccountStatusPage deleteAccount() {
        click(DELETE_ACCOUNT);
        return new AccountStatusPage();
    }
}
