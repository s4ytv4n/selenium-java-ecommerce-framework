package io.github.s4ytv4n.api;

import io.github.s4ytv4n.base.BaseApiTest;
import io.github.s4ytv4n.data.UserFactory;
import io.github.s4ytv4n.models.User;
import io.qameta.allure.Feature;
import io.restassured.http.Method;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Account API")
public class AccountValidationApiTest extends BaseApiTest {

    @Test(groups = "negative", description = "verifyLogin with unknown credentials returns 404")
    public void unknownCredentialsAreRejected() {
        ApiResponse response = api.verifyLogin("nobody.qa@example.com", "wrong-password");

        assertThat(response.responseCode()).isEqualTo(404);
        assertThat(response.message()).isEqualTo("User not found!");
    }

    @Test(groups = "negative", description = "verifyLogin without email is a bad request")
    public void verifyLoginWithoutEmailIsBadRequest() {
        ApiResponse response = api.verifyLoginWithoutEmail("any-password");

        assertThat(response.responseCode()).isEqualTo(400);
        assertThat(response.message()).isEqualTo("Bad request, email or password parameter is missing in POST request.");
    }

    @Test(groups = "negative", description = "DELETE /verifyLogin is not supported")
    public void deleteVerifyLoginIsNotSupported() {
        ApiResponse response = api.send(Method.DELETE, "/verifyLogin", Map.of());

        assertThat(response.responseCode()).isEqualTo(405);
        assertThat(response.message()).isEqualTo("This request method is not supported.");
    }

    @Test(groups = "negative", description = "Creating an account with an existing email is rejected")
    public void duplicateEmailIsRejected() {
        User existing = UserFactory.create();
        api.createAccount(existing);
        try {
            ApiResponse response = api.createAccount(UserFactory.create().withEmail(existing.email()));

            assertThat(response.responseCode()).isEqualTo(400);
            assertThat(response.message()).isEqualTo("Email already exists!");
        } finally {
            api.deleteAccount(existing.email(), existing.password());
        }
    }
}
