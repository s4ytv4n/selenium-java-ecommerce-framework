package io.github.s4ytv4n.pages;

import io.github.s4ytv4n.models.ProductLine;
import io.github.s4ytv4n.utils.Prices;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ProductsPage extends BasePage {

    private static final By SEARCH_INPUT = By.id("search_product");
    private static final By SEARCH_BUTTON = By.id("submit_search");
    private static final By SECTION_TITLE = By.cssSelector(".features_items h2.title");
    private static final By PRODUCT_CARDS = By.cssSelector(".features_items .product-image-wrapper");
    private static final By CARD_NAME = By.cssSelector(".productinfo p");
    private static final By CARD_PRICE = By.cssSelector(".productinfo h2");
    private static final By CARD_ADD_TO_CART = By.cssSelector(".productinfo a.add-to-cart");

    @Step("Open products page")
    public ProductsPage open() {
        openPath("/products");
        visible(SECTION_TITLE);
        return this;
    }

    @Step("Search for '{term}'")
    public ProductsPage search(String term) {
        type(SEARCH_INPUT, term);
        click(SEARCH_BUTTON);
        wait.until(d -> text(SECTION_TITLE).equalsIgnoreCase("Searched Products"));
        return this;
    }

    public String sectionTitle() {
        return text(SECTION_TITLE);
    }

    public int productCount() {
        return driver.findElements(PRODUCT_CARDS).size();
    }

    public List<String> productNames() {
        return driver.findElements(PRODUCT_CARDS).stream()
                .map(card -> card.findElement(CARD_NAME).getText().trim())
                .toList();
    }

    public ProductLine product(int index) {
        WebElement card = card(index);
        return ProductLine.listed(
                card.findElement(CARD_NAME).getText().trim(),
                Prices.parse(card.findElement(CARD_PRICE).getText()));
    }

    @Step("Add product #{index} to the cart")
    public CartModal addToCart(int index) {
        WebElement button = card(index).findElement(CARD_ADD_TO_CART);
        // Hovering a card slides an overlay over this button, so a native click is often intercepted.
        // The site binds the same handler to the button, so a JS click is a faithful user action here.
        js("arguments[0].scrollIntoView({block: 'center'}); arguments[0].click();", button);
        return new CartModal();
    }

    private WebElement card(int index) {
        List<WebElement> cards = wait.until(d -> {
            List<WebElement> found = d.findElements(PRODUCT_CARDS);
            return found.size() > index ? found : null;
        });
        return cards.get(index);
    }
}
