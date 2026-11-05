package io.github.s4ytv4n.ui;

import io.github.s4ytv4n.base.BaseUiTest;
import io.github.s4ytv4n.models.ProductLine;
import io.github.s4ytv4n.models.User;
import io.github.s4ytv4n.pages.CartModal;
import io.github.s4ytv4n.pages.CartPage;
import io.github.s4ytv4n.pages.LoginPage;
import io.github.s4ytv4n.pages.ProductDetailsPage;
import io.github.s4ytv4n.pages.ProductsPage;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Shopping cart")
public class CartTest extends BaseUiTest {

    @Test(groups = "smoke", description = "Products added from the listing appear in the cart with correct prices")
    public void productsFromListingAppearInCart() {
        ProductsPage products = new ProductsPage().open();
        ProductLine first = products.product(0);
        ProductLine second = products.product(1);

        CartModal modal = products.addToCart(0);
        assertThat(modal.title()).isEqualTo("Added!");
        CartPage cart = modal.continueShopping().addToCart(1).viewCart();

        assertThat(cart.items()).containsExactly(first, second);
    }

    @Test(description = "Quantity chosen on the product page is carried to the cart and the total is correct")
    public void quantityFromProductPageIsCarriedToCart() {
        int quantity = 4;
        ProductDetailsPage details = new ProductDetailsPage().open(1);
        ProductLine product = details.product();

        CartPage cart = details.addToCart(quantity).viewCart();

        assertThat(cart.items()).containsExactly(product.withQuantity(quantity));
    }

    @Test(description = "Removing the only product empties the cart")
    public void removingOnlyProductEmptiesCart() {
        ProductsPage products = new ProductsPage().open();
        ProductLine product = products.product(0);

        CartPage cart = products.addToCart(0).viewCart().remove(product.name());

        assertThat(cart.emptyCartMessage()).contains("Cart is empty!");
    }

    @Test(description = "Guest cart is kept after the customer logs in")
    public void cartIsKeptAfterLogin() {
        User user = registeredUser();
        ProductsPage products = new ProductsPage().open();
        ProductLine product = products.product(2);
        products.addToCart(2).continueShopping();

        new LoginPage().open().login(user.email(), user.password()).header().loggedInUserName();

        assertThat(new CartPage().open().contains(product.name())).isTrue();
    }
}
