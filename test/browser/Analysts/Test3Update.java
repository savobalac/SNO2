package browser.Analysts;

import org.fluentlenium.adapter.FluentTest;
import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.Utils;

import java.util.concurrent.TimeUnit;

import static org.fest.assertions.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withText;

/**
 * Tests the analyst can be edited using a browser (via Selenium and FluentLenium API).
 *
 * Date: 19/11/13
 * Time: 16:36
 *
 * @author      Sav Balac
 * @version     1.0
 */
public class Test3Update extends FluentTest {


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
     * @verifies That the edit analyst page shows the new analyst and checks the expertise field is updated.
     */
    @Test
    public void testEditAnalyst() {

        // Go to the list analysts page
        goTo("http://localhost:9000/analysts");

        // Get the id of the newly-created analyst
        FluentList<FluentWebElement> newAnalystLinks = find(".analystId", withText("New Analyst Created by Testing"));
        String id;
        if (newAnalystLinks.size() == 1) { // There should only be one analyst with that name
            id = newAnalystLinks.first().getId();
            goTo("http://localhost:9000/analysts/" + id);

            // Check we are editing the new analyst
            assertThat(title().contentEquals("New Analyst Created by Testing"));
            assertThat(find("#analystName").contains("Analyst: New Analyst Created by Testing")); // Check the main heading

            // Edit the expertise field (with the current date and time) and save
            String currentDateTime = Utils.formatUpdatedTimestamp(Utils.getCurrentDateTime());
            fill("#expertise").with(currentDateTime);
            submit("#analystForm");

            // Check that we're on the list analysts page and that the analyst was updated
            assertThat(url().contentEquals("http://localhost:9000/analysts"));
            assertThat(pageSource().contains("Analyst: New Analyst Created by Testing successfully updated."));

            // Check the expertise field was updated
            goTo("http://localhost:9000/analysts/" + id);
            assertThat(find("#expertise").contains(currentDateTime));
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
