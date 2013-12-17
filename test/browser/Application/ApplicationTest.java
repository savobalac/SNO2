package browser.Application;

import org.fluentlenium.adapter.FluentTest;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the login and index pages using a browser (via Selenium and FluentLenium API).
 *
 * Date: 06/11/13
 * Time: 11:15
 *
 * @author      Sav Balac
 * @version     1.0
 */
public class ApplicationTest extends FluentTest {


    /**
     * @verifies That signing in sends the user to the home page.
     */
    @Test
    public void testLoginToHomePage() {
        goTo("http://localhost:9000/");
        fill("#username").with("savbalac");
        fill("#password").with("h0tsp0t");
        submit("#signIn");
        assertTrue("URL not equal to the home page", url().contentEquals("http://localhost:9000/")); // The home (index) page
        assertTrue("Title not equal to SNO2", title().contentEquals("SNO2"));
        assertTrue("Not the Home page", pageSource().contains("Home"));
        assertTrue("User is not Savo Balac", pageSource().contains("Sav Balac"));
    }


    /**
     * @verifies That signing out sends the user to the sign in page.
     */
    @Test
    public void testSignOut() {
        goTo("http://localhost:9000/logout");
        assertTrue("URL not equal to the sign in page", url().contentEquals("http://localhost:9000/login")); // The sign in page
        assertTrue("Title not equal to Sign in to SNO2", title().contentEquals("Sign in to SNO2"));
        assertTrue("Not the Sign in page", pageSource().contains("Sign in to SNO2"));
        assertTrue("No signed out message displayed", pageSource().contains("You have signed out."));
    }


}
