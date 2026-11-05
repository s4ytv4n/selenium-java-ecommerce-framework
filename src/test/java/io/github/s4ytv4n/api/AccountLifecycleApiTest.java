package io.github.s4ytv4n.api;

import io.github.s4ytv4n.base.BaseApiTest;
import io.github.s4ytv4n.data.UserFactory;
import io.github.s4ytv4n.models.User;
import io.qameta.allure.Feature;
import org.hamcrest.MatcherAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

/** create -> verify -> read -> update -> delete. Steps depend on each other, so the class runs in one thread. */
@Feature("Account API")
@Test(singleThreaded = true)
public class AccountLifecycleApiTest extends BaseApiTest {

    private final User user = UserFactory.create();

    @Test(groups = "smoke", description = "Create account")
    public void createAccount() {
        ApiResponse response = api.createAccount(user);

        assertThat(response.responseCode()).isEqualTo(201);
        assertThat(response.message()).isEqualTo("User created!");
    }

    @Test(dependsOnMethods = "createAccount", description = "Created credentials are accepted by verifyLogin")
    public void credentialsAreAccepted() {
        ApiResponse response = api.verifyLogin(user.email(), user.password());

        assertThat(response.responseCode()).isEqualTo(200);
        assertThat(response.message()).isEqualTo("User exists!");
    }

    @Test(dependsOnMethods = "createAccount", description = "User details contain the data sent on creation")
    public void userDetailsMatchCreatedData() {
        ApiResponse response = api.getUserByEmail(user.email());

        assertThat(response.responseCode()).isEqualTo(200);
        MatcherAssert.assertThat(response.body(), matchesJsonSchemaInClasspath("schemas/user-detail.json"));
        assertThat(response.json().getString("user.email")).isEqualTo(user.email());
        assertThat(response.json().getString("user.first_name")).isEqualTo(user.firstName());
        assertThat(response.json().getString("user.last_name")).isEqualTo(user.lastName());
        assertThat(response.json().getString("user.city")).isEqualTo(user.city());
        assertThat(response.json().getString("user.country")).isEqualTo(user.country());
    }

    @Test(dependsOnMethods = {"credentialsAreAccepted", "userDetailsMatchCreatedData"},
            description = "Update account changes the stored data")
    public void updateAccountChangesStoredData() {
        User updated = user.withCityAndCompany("Plovdiv", "Updated Company Ltd");

        ApiResponse response = api.updateAccount(updated);
        assertThat(response.responseCode()).isEqualTo(200);
        assertThat(response.message()).isEqualTo("User updated!");

        ApiResponse details = api.getUserByEmail(user.email());
        assertThat(details.json().getString("user.city")).isEqualTo("Plovdiv");
        assertThat(details.json().getString("user.company")).isEqualTo("Updated Company Ltd");
    }

    @Test(dependsOnMethods = "updateAccountChangesStoredData", description = "Deleted account can no longer log in")
    public void deleteAccountRevokesLogin() {
        ApiResponse deleted = api.deleteAccount(user.email(), user.password());
        assertThat(deleted.responseCode()).isEqualTo(200);
        assertThat(deleted.message()).isEqualTo("Account deleted!");

        assertThat(api.verifyLogin(user.email(), user.password()).responseCode()).isEqualTo(404);
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() {
        // No-op when the lifecycle completed; removes the account if a step failed midway.
        api.deleteAccount(user.email(), user.password());
    }
}
