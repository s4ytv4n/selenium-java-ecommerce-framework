package io.github.s4ytv4n.api;

import io.github.s4ytv4n.base.BaseApiTest;
import io.qameta.allure.Feature;
import io.restassured.http.Method;
import org.hamcrest.MatcherAssert;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

@Feature("Products API")
public class ProductsApiTest extends BaseApiTest {

    private static final String NOT_SUPPORTED = "This request method is not supported.";

    @Test(groups = "smoke", description = "GET /productsList returns the catalogue matching the JSON schema")
    public void productsListMatchesSchema() {
        ApiResponse response = api.getProducts();

        assertThat(response.responseCode()).isEqualTo(200);
        MatcherAssert.assertThat(response.body(), matchesJsonSchemaInClasspath("schemas/products-list.json"));

        List<Integer> ids = response.json().getList("products.id", Integer.class);
        assertThat(new HashSet<>(ids)).as("product ids must be unique").hasSameSizeAs(ids);
    }

    @Test(groups = "negative", description = "POST /productsList is not supported")
    public void postProductsListIsNotSupported() {
        ApiResponse response = api.send(Method.POST, "/productsList", Map.of());

        assertThat(response.responseCode()).isEqualTo(405);
        assertThat(response.message()).isEqualTo(NOT_SUPPORTED);
    }

    @Test(groups = "smoke", description = "GET /brandsList returns brands matching the JSON schema")
    public void brandsListMatchesSchema() {
        ApiResponse response = api.getBrands();

        assertThat(response.responseCode()).isEqualTo(200);
        MatcherAssert.assertThat(response.body(), matchesJsonSchemaInClasspath("schemas/brands-list.json"));
    }

    @Test(groups = "negative", description = "PUT /brandsList is not supported")
    public void putBrandsListIsNotSupported() {
        ApiResponse response = api.send(Method.PUT, "/brandsList", Map.of());

        assertThat(response.responseCode()).isEqualTo(405);
        assertThat(response.message()).isEqualTo(NOT_SUPPORTED);
    }

    @Test(description = "POST /searchProduct returns only products that exist in the catalogue")
    public void searchReturnsOnlyCatalogueProducts() {
        ApiResponse search = api.searchProducts("top");
        List<Integer> catalogueIds = api.getProducts().json().getList("products.id", Integer.class);

        assertThat(search.responseCode()).isEqualTo(200);
        assertThat(search.json().getList("products.id", Integer.class))
                .isNotEmpty()
                .isSubsetOf(catalogueIds);
    }

    @Test(groups = "negative", description = "POST /searchProduct without the search parameter is a bad request")
    public void searchWithoutTermIsBadRequest() {
        ApiResponse response = api.searchProductsWithoutTerm();

        assertThat(response.responseCode()).isEqualTo(400);
        assertThat(response.message()).isEqualTo("Bad request, search_product parameter is missing in POST request.");
    }
}
