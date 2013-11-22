package browser.Analysts;

import org.fluentlenium.adapter.FluentTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

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
        assertThat(title().contentEquals("New Analyst"));
        assertThat(find("#newAnalyst").contains("New Analyst")); // Check the main heading

        // Add the required fields and save
        fill("#firstname").with("New Analyst");
        fill("#lastname").with("Created by Testing");
        submit("#analystForm");

        // Check that we're on the list analysts page and that a new analyst has been created
        assertThat(url().contentEquals("http://localhost:9000/analysts"));
        assertThat(pageSource().contains("Analyst: New Analyst Created by Testing has been created."));
    }


    /**
     * Logs out.
     */
    @After
    public void tearDown() {
        goTo("http://localhost:9000/logout"); // Log out
    }


}
