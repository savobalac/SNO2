package browser;

import org.fluentlenium.adapter.FluentTest;
import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.Utils;

import static org.fest.assertions.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withText;

/**
 * Tests the user can be edited using a browser (via Selenium and FluentLenium API).
 *
 * Date: 19/11/13
 * Time: 11:52
 *
 * @author      Sav Balac
 * @version     %I%, %G%
 * @since       1.0
 */
public class UsersTest3Update extends FluentTest {


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
     * @verifies That the edit user page shows the new user, edits the email field and saves.
     */
    @Test
    public void testEditUser() {

        // Go to the list users page
        goTo("http://localhost:9000/users");

        // Get the id of the newly-created user
        FluentList<FluentWebElement> newUserLinks = find(".userId", withText().contains("New User Created by Testing"));
        String id;
        if (newUserLinks.size() == 1) { // There should only be one user with that name
            id = newUserLinks.first().getId();
            goTo("http://localhost:9000/users/" + id);

            // Check we are editing the new user
            assertThat(title().contentEquals("New User Created by Testing"));
            assertThat(find("#userName").contains("User: New User Created by Testing")); // Check the main heading

            // Edit the email field (with the current date and time) and save
            String currentDateTime = Utils.formatCreatedTimestamp(Utils.getCurrentDateTime());
            fill("#email").with(currentDateTime);
            submit("#formUpload");

            // Check the email field was updated
            goTo("http://localhost:9000/users/" + id);
            assertThat(find("#email").contains(currentDateTime));

            // Change the user's password
            goTo("http://localhost:9000/users/" + id + "/password");
            fill("#newPassword").with("newpassword");
            fill("#confirmPassword").with("newpassword");
            submit("#formUpload");

            // Check the password was updated
            assertThat(pageSource().contains("Password successfully updated."));

            // Sign out and login using the new password
            goTo("http://localhost:9000/logout");
            fill("#username").with("newuser");
            fill("#password").with("newpassword");
            submit("#signIn");

            // Check that the user is logged in
            assertThat(url().contentEquals("http://localhost:9000/")); // The home (index) page
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
