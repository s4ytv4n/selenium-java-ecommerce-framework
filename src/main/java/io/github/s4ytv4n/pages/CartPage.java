package io.github.s4ytv4n.pages;

import io.github.s4ytv4n.models.ProductLine;
import io.github.s4ytv4n.utils.Prices;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class CartPage extends BasePage {

    private static final By ROWS = By.cssSelector("#cart_info_table tbody tr");
    private static final By ROW_NAME = By.cssSelector(".cart_description h4 a");
    private static final By ROW_PRICE = By.cssSelector(".cart_price p");
    private static final By ROW_QUANTITY = By.cssSelector(".cart_quantity button");
    private static final By ROW_TOTAL = By.cssSelector(".cart_total_price");
    private static final By ROW_DELETE = By.cssSelector(".cart_quantity_delete");
    private static final By EMPTY_CART = By.id("empty_cart");

    @Step("Open cart")
    public CartPage open() {
        openPath("/view_cart");
        return this;
    }

    public List<ProductLine> items() {
        wait.until(d -> !d.findElements(ROWS).isEmpty() || isVisible(EMPTY_CART));
        return driver.findElements(ROWS).stream().map(CartPage::toLine).toList();
    }

    public boolean contains(String productName) {
        return items().stream().anyMatch(item -> item.name().equals(productName));
    }

    @Step("Remove '{productName}' from the cart")
    public CartPage remove(String productName) {
        WebElement row = driver.findElements(ROWS).stream()
                .filter(r -> r.findElement(ROW_NAME).getText().trim().equals(productName))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("'" + productName + "' is not in the cart"));
        row.findElement(ROW_DELETE).click();
        wait.until(ExpectedConditions.stalenessOf(row));
        return this;
    }

    public String emptyCartMessage() {
        return text(EMPTY_CART);
    }

    private static ProductLine toLine(WebElement row) {
        return new ProductLine(
                row.findElement(ROW_NAME).getText().trim(),
                Prices.parse(row.findElement(ROW_PRICE).getText()),
                Integer.parseInt(row.findElement(ROW_QUANTITY).getText().trim()),
                Prices.parse(row.findElement(ROW_TOTAL).getText()));
    }
}
