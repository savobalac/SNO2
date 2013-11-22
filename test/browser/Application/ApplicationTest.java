package browser.Application;

import org.fluentlenium.adapter.FluentTest;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

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
        assertThat(url().contentEquals("http://localhost:9000/")); // The home (index) page
        assertThat(title().contentEquals("SNO2"));
        assertThat(pageSource().contains("Home"));
        assertThat(pageSource().contains("Savo Balac"));
    }


    /**
     * @verifies That signing out sends the user to the sign in page.
     */
    @Test
    public void testSignOut() {
        goTo("http://localhost:9000/logout");
        assertThat(url().contentEquals("http://localhost:9000/login")); // The sign in page
        assertThat(title().contentEquals("Sign in to SNO2"));
        assertThat(pageSource().contains("Sign in to SNO2"));
        assertThat(pageSource().contains("You have signed out."));
    }


}
