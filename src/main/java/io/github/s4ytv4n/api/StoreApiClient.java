package io.github.s4ytv4n.api;

import io.github.s4ytv4n.config.Config;
import io.github.s4ytv4n.models.User;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.LinkedHashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

/** Thin client over the store's REST API. Every request and response is attached to the Allure report. */
public class StoreApiClient {

    private final RequestSpecification spec = new RequestSpecBuilder()
            .setBaseUri(Config.apiUrl())
            .addFilter(new AllureRestAssured())
            .build();

    @Step("API: get all products")
    public ApiResponse getProducts() {
        return send(Method.GET, "/productsList", Map.of());
    }

    @Step("API: get all brands")
    public ApiResponse getBrands() {
        return send(Method.GET, "/brandsList", Map.of());
    }

    @Step("API: search products for '{term}'")
    public ApiResponse searchProducts(String term) {
        return send(Method.POST, "/searchProduct", Map.of("search_product", term));
    }

    @Step("API: search products without a search term")
    public ApiResponse searchProductsWithoutTerm() {
        return send(Method.POST, "/searchProduct", Map.of());
    }

    @Step("API: create account {user.email}")
    public ApiResponse createAccount(User user) {
        return send(Method.POST, "/createAccount", accountForm(user));
    }

    @Step("API: update account {user.email}")
    public ApiResponse updateAccount(User user) {
        return send(Method.PUT, "/updateAccount", accountForm(user));
    }

    @Step("API: delete account {email}")
    public ApiResponse deleteAccount(String email, String password) {
        return send(Method.DELETE, "/deleteAccount", Map.of("email", email, "password", password));
    }

    @Step("API: verify login for {email}")
    public ApiResponse verifyLogin(String email, String password) {
        return send(Method.POST, "/verifyLogin", Map.of("email", email, "password", password));
    }

    @Step("API: verify login without email")
    public ApiResponse verifyLoginWithoutEmail(String password) {
        return send(Method.POST, "/verifyLogin", Map.of("password", password));
    }

    @Step("API: get user details for {email}")
    public ApiResponse getUserByEmail(String email) {
        Response response = given(spec).queryParam("email", email).get("/getUserDetailByEmail");
        return toApiResponse(response);
    }

    /** Low-level entry point, also used by tests that check unsupported HTTP methods. */
    public ApiResponse send(Method method, String path, Map<String, String> form) {
        RequestSpecification request = given(spec);
        if (!form.isEmpty()) {
            request = request.contentType(ContentType.URLENC.withCharset("UTF-8")).formParams(form);
        }
        return toApiResponse(request.request(method, path));
    }

    private static ApiResponse toApiResponse(Response response) {
        String body = response.asString();
        JsonPath json;
        try {
            json = JsonPath.from(body);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Expected JSON (HTTP " + response.statusCode() + "), got: "
                    + body.substring(0, Math.min(body.length(), 200)), e);
        }
        return new ApiResponse(response.statusCode(), json.getInt("responseCode"), json.getString("message"), body);
    }

    private static Map<String, String> accountForm(User user) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("name", user.name());
        form.put("email", user.email());
        form.put("password", user.password());
        form.put("title", user.title());
        form.put("birth_date", user.birthDay());
        form.put("birth_month", user.birthMonth());
        form.put("birth_year", user.birthYear());
        form.put("firstname", user.firstName());
        form.put("lastname", user.lastName());
        form.put("company", user.company());
        form.put("address1", user.address1());
        form.put("address2", user.address2());
        form.put("country", user.country());
        form.put("zipcode", user.zipcode());
        form.put("state", user.state());
        form.put("city", user.city());
        form.put("mobile_number", user.mobileNumber());
        return form;
    }
}
