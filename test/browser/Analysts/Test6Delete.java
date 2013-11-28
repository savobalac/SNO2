package browser.Analysts;

import org.fluentlenium.adapter.FluentTest;
import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.fluentlenium.core.filter.FilterConstructor.withText;

/**
 * Tests an analyst can be deleted using a browser (via Selenium and FluentLenium API).
 *
 * Date: 19/11/13
 * Time: 16:48
 *
 * @author      Sav Balac
 * @version     1.0
 */
public class Test6Delete extends FluentTest {


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
     * @verifies That the new analyst created earlier was deleted.
     */
    @Test
    public void testDeleteNewAnalyst() {

        // Go to the list analysts page
        goTo("http://localhost:9000/analysts");

        // Get the id of the newly-created analyst
        FluentList<FluentWebElement> newAnalystLinks = find(".analystId", withText("A New Analyst, Created by Testing"));
        String id;
        if (newAnalystLinks.size() == 1) { // There should only be one analyst with that name
            id = newAnalystLinks.first().getId();
            goTo("http://localhost:9000/analysts/" + id);
            submit("#deleteForm");

            // Check that we're on the list analysts page
            assertTrue("URL not the list analysts page", url().contentEquals("http://localhost:9000/analysts"));
            assertTrue("Title not Analysts", title().contentEquals("Analysts"));

            // Check that the analyst is no longer in the list
            assertTrue("Analyst not deleted", pageSource().contains("Analyst: Created by Testing A New Analyst deleted."));
            assertFalse("Analyst still in the list", pageSource().contains("A New Analyst, Created by Testing"));
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
