package browser.Analysts;

import org.fluentlenium.adapter.FluentTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Tests the list analyst page using a browser (via Selenium and FluentLenium API).
 *
 * Date: 19/11/13
 * Time: 16:32
 *
 * @author      Sav Balac
 * @version     %I%, %G%
 * @since       1.0
 */
public class Test2Read extends FluentTest {


    /**
     * Goes to the list analyst page (via login page).
     */
    @Before
    public void setUp() {
        // Log in first
        goTo("http://localhost:9000/analysts"); // Will redirect to the login page
        fill("#username").with("savbalac");
        fill("#password").with("h0tsp0t");
        submit("#signIn");
    }


    /**
     * @verifies That the list analyst page shows analyst Sav Balac.
     */
    @Test
    public void testListAnalysts() {

        // Check we're on the analysts page
        goTo("http://localhost:9000/analysts");
        assertThat(url().contentEquals("http://localhost:9000/analysts"));
        assertThat(title().contentEquals("Analysts"));
        assertThat(find("#homeTitle").contains("Analysts")); // Check the main heading

        // Check that the new analyst is in the list
        assertThat(pageSource().contains("New Analyst Created by Testing"));
    }


    /**
     * Logs out.
     */
    @After
    public void tearDown() {
        goTo("http://localhost:9000/logout"); // Log out
    }


}
