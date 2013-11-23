package browser.Analysts;

import org.fluentlenium.adapter.FluentTest;
import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
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
public class Test5Delete extends FluentTest {


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
        FluentList<FluentWebElement> newAnalystLinks = find(".analystId", withText("Created by Testing, New Analyst"));
        String id;
        if (newAnalystLinks.size() == 1) { // There should only be one analyst with that name
            id = newAnalystLinks.first().getId();
            goTo("http://localhost:9000/analysts/" + id);
            submit("#deleteForm");

            // Check that we're on the list analysts page
            assertThat(url().contentEquals("http://localhost:9000/analysts"));
            assertThat(title().contentEquals("Analysts"));
            assertThat(find("#homeTitle").contains("Analysts")); // Check the main heading

            // Check that the analyst is no longer in the list
            assertThat(pageSource().contains("Analyst: New Analyst Created by Testing deleted."));
            assertThat(!pageSource().contains("Created by Testing, New Analyst"));
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
