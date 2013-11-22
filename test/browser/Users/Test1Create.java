package browser.Users;

import org.fluentlenium.adapter.FluentTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Tests a user can be created using a browser (via Selenium and FluentLenium API).
 *
 * Date: 19/11/13
 * Time: 11:42
 *
 * @author      Sav Balac
 * @version     %I%, %G%
 * @since       1.0
 */
public class Test1Create extends FluentTest {


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
     * @verifies That a new user can be created.
     */
    @Test
    public void testANewUser() {

        // Check that the New User page is showing
        goTo("http://localhost:9000/users/new");
        assertThat(title().contentEquals("New User"));
        assertThat(find("#newUser").contains("New User")); // Check the main heading

        // Add the required fields and save
        fill("#fullname").with("New User Created by Testing");
        fill("#email").with("newuser@testing.com");
        fill("#username").with("newuser");
        fill("#password").with("password");
        fill("#confirmPassword").with("password");
        submit("#userForm");

        // Check that we're on the list users page and that a new user has been created
        assertThat(url().contentEquals("http://localhost:9000/users"));
        assertThat(pageSource().contains("User: New User Created by Testing has been created."));
    }


    /**
     * Logs out.
     */
    @After
    public void tearDown() {
        goTo("http://localhost:9000/logout"); // Log out
    }


}
