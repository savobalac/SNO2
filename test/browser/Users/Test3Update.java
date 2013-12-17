package browser.Users;

import org.fluentlenium.adapter.FluentTest;
import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.Utils;

import static org.junit.Assert.*;
import static org.fluentlenium.core.filter.FilterConstructor.withText;

/**
 * Tests the user can be edited using a browser (via Selenium and FluentLenium API).
 *
 * Date: 19/11/13
 * Time: 11:52
 *
 * @author      Sav Balac
 * @version     1.0
 */
public class Test3Update extends FluentTest {


    /**
     * Goes to the list users page (via login page).
     */
    @Before
    public void setUp() {
        // Log in first
        goTo("http://localhost:9000/users"); // Will redirect to the login page
        fill("#username").with("savbalac");
        fill("#password").with("h0tsp0t");
        submit("#signIn");
    }


    /**
     * @verifies That the edit user page shows the new user, checks the the email field is updated,
     *           updates the password and checks the user can log in with it.
     */
    @Test
    public void testEditUser() {

        // Go to the list users page
        goTo("http://localhost:9000/users");

        // Get the id of the newly-created user
        FluentList<FluentWebElement> newUserLinks = find(".userId", withText().contains("A New User Created by Testing"));
        String id;
        if (newUserLinks.size() == 1) { // There should only be one user with that name
            id = newUserLinks.first().getId();
            goTo("http://localhost:9000/users/" + id);

            // Check we are editing the new user
            assertTrue("Page title not the new user", title().contentEquals("A New User Created by Testing"));

            // Edit the email field (with the current date and time) and save
            String currentDateTime = Utils.formatUpdatedTimestamp(Utils.getCurrentDateTime());
            fill("#email").with(currentDateTime);
            submit("#userForm");

            // Check that we're on the list users page and that the user was updated
            assertTrue("URL not the list users page", url().contentEquals("http://localhost:9000/users"));
            assertTrue("User not updated", pageSource().contains("User: A New User Created by Testing updated."));

            // Check the email field was updated
            goTo("http://localhost:9000/users/" + id);
            assertTrue("Email field not updated", find("#email").getValue().equals(currentDateTime));

            // Change the user's password
            goTo("http://localhost:9000/users/" + id + "/password");
            fill("#newPassword").with("newpassword");
            fill("#confirmPassword").with("newpassword");
            submit("#userForm");

            // Check that we're on the user's page and that the password was updated
            assertTrue("URL not the user's page", url().contentEquals("http://localhost:9000/users/" + id));
            assertTrue("Password not updated", pageSource().contains("Password updated."));

            // Log out and sign in using the new password
            goTo("http://localhost:9000/logout");
            fill("#username").with("newuser");
            fill("#password").with("newpassword");
            submit("#signIn");

            // Check that the user is logged in
            assertTrue("User not logged in", url().contentEquals("http://localhost:9000/")); // The home (index) page
            assertTrue("Page title not the home page", title().contentEquals("SNO2"));
            assertTrue("Page doesn't contain Home", pageSource().contains("Home"));
            assertTrue("User name not in page", pageSource().contains("A New User Created by Testing"));
        }
    }


    /**
     * Logs out.
     */
    @After
    public void tearDown() {
        goTo("http://localhost:9000/logout"); // Log out
    }


}
