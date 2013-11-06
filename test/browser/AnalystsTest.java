package browser;

import org.fluentlenium.adapter.FluentTest;
import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.Utils;
import static org.fest.assertions.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sav Balac
 * Date: 06/11/13
 * Time: 12:21
 * Description: Tests the list and edit analyst pages using a browser (via Selenium and FluentLenium API).
 * To change this template use File | Settings | File Templates.
 */
public class AnalystsTest extends FluentTest {


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
        assertThat(url().contentEquals("http://localhost:9000/analysts"));
        assertThat(title().contentEquals("Analysts"));
        assertThat(find("#homeTitle").contains("Analysts")); // Check the main heading

        // Check that analyst Sav Balac is in the list
        assertThat(pageSource().contains("Sav Balac"));
    }


    /**
     * @verifies That the edit analyst page shows analyst Sav Balac, edits the notes field and saves.
     */
    @Test
    public void testEditAnalyst() {

        // Check we are editing analyst Sav Balac
        goTo("http://localhost:9000/analysts/3"); // analyst id = 3
        assertThat(title().contentEquals("Sav Balac"));
        assertThat(find("#analystName").contains("Analyst: Sav Balac")); // Check the main heading

        // Edit the notes field (with the current date and time) and save
        String currentDateTime = Utils.formatCreatedTimestamp(Utils.getCurrentDateTime());
        fill("#notes").with(currentDateTime);
        submit("#formUpload");

        // Check the notes field was updated
        goTo("http://localhost:9000/analysts/3");
        assertThat(find("#notes").contains(currentDateTime));
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
        submit("#formUpload");
    }


    /**
     * @verifies That the new analyst created above was deleted.
     */
    @Test
    public void testDeleteNewAnalyst() {

        // Get the id of the newly-created analyst
        goTo("http://localhost:9000/analysts");
        FluentList<FluentWebElement> newAnalystLinks = find(".analystId", withText("New Analyst Created by Testing"));

        String id;
        if (newAnalystLinks.size() == 1) { // There should only be one analyst with that name
            id = newAnalystLinks.first().getId();
            goTo("http://localhost:9000/analysts/" + id);
            click("#DELETE");
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
