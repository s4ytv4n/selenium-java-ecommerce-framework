package io.github.s4ytv4n.pages;

import io.github.s4ytv4n.models.ProductLine;
import io.github.s4ytv4n.utils.Prices;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class ProductDetailsPage extends BasePage {

    private static final By NAME = By.cssSelector(".product-information h2");
    private static final By PRICE = By.cssSelector(".product-information span > span");
    private static final By QUANTITY = By.id("quantity");
    private static final By ADD_TO_CART = By.cssSelector(".product-information button.cart");

    @Step("Open product #{id}")
    public ProductDetailsPage open(int id) {
        openPath("/product_details/" + id);
        visible(NAME);
        return this;
    }

    public ProductLine product() {
        return ProductLine.listed(text(NAME), Prices.parse(text(PRICE)));
    }

    @Step("Add {quantity} pcs to the cart")
    public CartModal addToCart(int quantity) {
        type(QUANTITY, String.valueOf(quantity));
        click(ADD_TO_CART);
        return new CartModal();
    }
}
