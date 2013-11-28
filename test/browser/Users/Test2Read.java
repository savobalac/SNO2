package browser.Users;

import org.fluentlenium.adapter.FluentTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the list user page using a browser (via Selenium and FluentLenium API).
 *
 * Date: 19/11/13
 * Time: 11:49
 *
 * @author      Sav Balac
 * @version     1.0
 */
public class Test2Read extends FluentTest {


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
     * @verifies That the list user page shows the newly-created user.
     */
    @Test
    public void testListUsers() {

        // Check we're on the users page
        goTo("http://localhost:9000/users");
        assertTrue("URL not the list users page", url().contentEquals("http://localhost:9000/users"));
        assertTrue("Page title not Users", title().contentEquals("Users"));

        // Check that the new user is in the list
        assertTrue("New user not in the list", pageSource().contains("A New User Created by Testing"));
    }


    /**
     * Logs out.
     */
    @After
    public void tearDown() {
        goTo("http://localhost:9000/logout"); // Log out
    }


}
