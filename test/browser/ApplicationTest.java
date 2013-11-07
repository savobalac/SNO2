package browser;

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
 * @version     %I%, %G%
 * @since       1.0
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
        assertThat(pageSource().contains("Donec id elit non mi porta gravida at eget metus."));
    }


}