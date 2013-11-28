package browser.Analysts;

import org.fluentlenium.adapter.FluentTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the list analyst page using a browser (via Selenium and FluentLenium API).
 *
 * Date: 19/11/13
 * Time: 16:32
 *
 * @author      Sav Balac
 * @version     1.0
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
     * @verifies That the list analyst page shows the new analyst.
     */
    @Test
    public void testListAnalysts() {

        // Check we're on the analysts page
        goTo("http://localhost:9000/analysts");
        assertTrue("URL not equal to the analysts page", url().contentEquals("http://localhost:9000/analysts"));
        assertTrue("Page title not Analysts", title().contentEquals("Analysts"));

        // Check that the new analyst is in the list
        assertTrue("New analyst not in the list", pageSource().contains("A New Analyst, Created by Testing"));
    }


    /**
     * Logs out.
     */
    @After
    public void tearDown() {
        goTo("http://localhost:9000/logout"); // Log out
    }


}
