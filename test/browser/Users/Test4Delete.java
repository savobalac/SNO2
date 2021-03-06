package browser.Users;

import org.fluentlenium.adapter.FluentTest;
import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.fluentlenium.core.filter.FilterConstructor.withText;

/**
 * Tests a user can be deleted using a browser (via Selenium and FluentLenium API).
 *
 * Date: 19/11/13
 * Time: 12:13
 *
 * @author      Sav Balac
 * @version     1.0
 */
public class Test4Delete extends FluentTest {


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
     * @verifies That the new user created in test 1 was deleted.
     */
    @Test
    public void testDeleteNewUser() {

        // Go to the list users page
        goTo("http://localhost:9000/users");

        // Get the id of the newly-created user
        FluentList<FluentWebElement> newUserLinks = find(".userId", withText("A New User Created by Testing"));
        String id;
        if (newUserLinks.size() == 1) { // There should only be one user with that name
            id = newUserLinks.first().getId();
            goTo("http://localhost:9000/users/" + id);
            submit("#deleteForm");

            // Check the user is no longer in the list
            assertTrue("URL not the list users page", url().contentEquals("http://localhost:9000/users"));
            assertTrue("Title not Users", title().contentEquals("Users"));

            // Check that the user is no longer in the list
            assertTrue("User not deleted", pageSource().contains("User: A New User Created by Testing deleted."));
            newUserLinks = find(".userId", withText("A New User Created by Testing"));
            assertEquals("User still in the list", 0, newUserLinks.size());
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
