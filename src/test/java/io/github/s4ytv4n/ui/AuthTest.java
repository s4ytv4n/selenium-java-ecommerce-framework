package io.github.s4ytv4n.ui;

import io.github.s4ytv4n.base.BaseUiTest;
import io.github.s4ytv4n.data.UserFactory;
import io.github.s4ytv4n.models.User;
import io.github.s4ytv4n.pages.AccountStatusPage;
import io.github.s4ytv4n.pages.LoginPage;
import io.github.s4ytv4n.pages.SignupPage;
import io.qameta.allure.Feature;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Authentication")
public class AuthTest extends BaseUiTest {

    @Test(groups = {"smoke", "e2e"}, description = "New customer can register through the UI and delete the account")
    public void newCustomerCanRegisterAndDeleteAccount() {
        User user = UserFactory.create();
        cleanUpAfterTest(user);

        SignupPage signup = new LoginPage().open().startSignup(user.name(), user.email());
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(signup.prefilledName()).isEqualTo(user.name());
            soft.assertThat(signup.prefilledEmail()).isEqualTo(user.email());
        });

        AccountStatusPage status = signup.fillAccountInformation(user).createAccount();
        assertThat(status.accountCreatedTitle()).isEqualToIgnoringCase("Account Created!");

        String loggedInAs = status.continueToHome().header().loggedInUserName();
        assertThat(loggedInAs).isEqualTo(user.name());

        // The UI said "created" - check the data really landed in the backend.
        assertThat(api.getUserByEmail(user.email()).json().getString("user.city")).isEqualTo(user.city());

        AccountStatusPage deleted = new LoginPage().header().deleteAccount();
        assertThat(deleted.accountDeletedTitle()).isEqualToIgnoringCase("Account Deleted!");
    }

    @Test(groups = "smoke", description = "Registered customer can log in and log out")
    public void registeredCustomerCanLogInAndOut() {
        User user = registeredUser();

        String loggedInAs = new LoginPage().open().login(user.email(), user.password()).header().loggedInUserName();
        assertThat(loggedInAs).isEqualTo(user.name());

        LoginPage afterLogout = new LoginPage().header().logout();
        assertThat(afterLogout.currentUrl()).endsWith("/login");
        assertThat(afterLogout.header().isSignupLoginLinkVisible()).isTrue();
    }

    @Test(groups = "negative", description = "Login with a wrong password is rejected")
    public void loginWithWrongPasswordIsRejected() {
        User user = registeredUser();

        LoginPage page = new LoginPage().open().loginExpectingError(user.email(), user.password() + "-wrong");

        assertThat(page.loginError()).isEqualTo("Your email or password is incorrect!");
        assertThat(page.header().isLoggedIn()).isFalse();
    }

    @Test(groups = "negative", description = "Signup with an already registered email is rejected")
    public void signupWithExistingEmailIsRejected() {
        User user = registeredUser();

        LoginPage page = new LoginPage().open().startSignupExpectingError("Another Name", user.email());

        assertThat(page.signupError()).isEqualTo("Email Address already exist!");
    }

    @Test(groups = "negative", description = "Login form is not submitted with empty fields")
    public void loginFormRequiresBothFields() {
        LoginPage page = new LoginPage().open().submitEmptyLogin();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(page.isLoginEmailMissing()).as("email is required").isTrue();
            soft.assertThat(page.isLoginPasswordMissing()).as("password is required").isTrue();
            soft.assertThat(page.currentUrl()).endsWith("/login");
        });
    }
}
