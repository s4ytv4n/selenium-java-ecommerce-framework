package io.github.s4ytv4n.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

/** "Added!" confirmation shown after adding a product to the cart. */
public class CartModal extends BasePage {

    private static final By ROOT = By.id("cartModal");
    private static final By TITLE = By.cssSelector("#cartModal .modal-title");
    private static final By VIEW_CART = By.cssSelector("#cartModal a[href='/view_cart']");
    private static final By CONTINUE_SHOPPING = By.cssSelector("#cartModal button.close-modal");

    public String title() {
        return text(TITLE);
    }

    @Step("Continue shopping")
    public ProductsPage continueShopping() {
        click(CONTINUE_SHOPPING);
        waitUntilGone(ROOT);
        return new ProductsPage();
    }

    @Step("View cart from the confirmation modal")
    public CartPage viewCart() {
        click(VIEW_CART);
        return new CartPage();
    }
}
