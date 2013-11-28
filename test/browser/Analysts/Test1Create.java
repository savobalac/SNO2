package browser.Analysts;

import org.fluentlenium.adapter.FluentTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests an analyst can be created using a browser (via Selenium and FluentLenium API).
 *
 * Date: 19/11/13
 * Time: 16:21
 *
 * @author      Sav Balac
 * @version     1.0
 */
public class Test1Create extends FluentTest {


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
     * @verifies That a new analyst was created.
     */
    @Test
    public void testANewAnalyst() {

        // Check that the New Analyst page is showing
        goTo("http://localhost:9000/analysts/new");
        assertTrue("Title not equal to New Analyst", title().contentEquals("New Analyst"));

        // Add the required fields and save
        fill("#firstname").with("Created by Testing");
        fill("#lastname").with("A New Analyst");
        submit("#analystForm");

        // Check that we're on the list analysts page and that a new analyst has been created
        assertTrue("URL not equal to the list analyst page", url().contentEquals("http://localhost:9000/analysts"));
        assertTrue("Analyst not created", pageSource().contains("Analyst: Created by Testing A New Analyst has been created."));
    }


    /**
     * Logs out.
     */
    @After
    public void tearDown() {
        goTo("http://localhost:9000/logout"); // Log out
    }


}
