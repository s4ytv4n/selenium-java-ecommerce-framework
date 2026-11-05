package io.github.s4ytv4n.ui;

import io.github.s4ytv4n.base.BaseUiTest;
import io.github.s4ytv4n.pages.ProductsPage;
import io.qameta.allure.Feature;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Product catalogue")
public class ProductsTest extends BaseUiTest {

    @Test(groups = "smoke", description = "All products page lists the catalogue")
    public void allProductsPageListsCatalogue() {
        ProductsPage page = new ProductsPage().open();

        assertThat(page.sectionTitle()).isEqualToIgnoringCase("All Products");
        assertThat(page.productCount()).isGreaterThan(10);
    }

    @Test(description = "Catalogue shown in the UI matches the products API")
    public void catalogueMatchesProductsApi() {
        List<String> apiNames = api.getProducts().json().getList("products.name", String.class);

        List<String> uiNames = new ProductsPage().open().productNames();

        assertThat(uiNames).containsExactlyInAnyOrderElementsOf(trimmed(apiNames));
    }

    @DataProvider(name = "searchTerms", parallel = true)
    public Object[][] searchTerms() {
        return new Object[][]{{"top"}, {"jeans"}, {"dress"}, {"saree"}};
    }

    // Expected results come from the search API, so nothing is hard-coded and the test
    // survives catalogue changes while still proving UI and backend agree.
    @Test(dataProvider = "searchTerms", description = "UI search shows the same products as the search API")
    public void searchMatchesSearchApi(String term) {
        List<String> expected = api.searchProducts(term).json().getList("products.name", String.class);
        assertThat(expected).as("API returned no products for '%s'", term).isNotEmpty();

        List<String> actual = new ProductsPage().open().search(term).productNames();

        assertThat(actual).containsExactlyInAnyOrderElementsOf(trimmed(expected));
    }

    @Test(groups = "negative", description = "Search with no matches shows an empty result")
    public void searchWithoutMatchesShowsNothing() {
        ProductsPage page = new ProductsPage().open().search("no-such-product-xyz");

        assertThat(page.productCount()).isZero();
    }

    private static List<String> trimmed(List<String> names) {
        return names.stream().map(String::trim).toList();
    }
}
