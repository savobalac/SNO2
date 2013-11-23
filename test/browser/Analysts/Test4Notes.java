package browser.Analysts;

import org.fluentlenium.adapter.FluentTest;
import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.Utils;

import static org.fest.assertions.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withText;

/**
 * Tests a note can be created/read/updated/deleted for an analyst using a browser (via Selenium and FluentLenium API).
 *
 * Date: 20/11/13
 * Time: 10:32
 *
 * @author      Sav Balac
 * @version     1.0
 */
public class Test4Notes extends FluentTest {


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
     * @verifies That a new note can be created/read/updated/deleted for the analyst page.
     */
    @Test
    public void testNotes() {

        // Go to the list analysts page
        goTo("http://localhost:9000/analysts");

        // Get the id of the newly-created analyst
        FluentList<FluentWebElement> newAnalystLinks = find(".analystId", withText("Created by Testing, New Analyst"));
        String id;
        if (newAnalystLinks.size() == 1) { // There should only be one analyst with that name
            id = newAnalystLinks.first().getId();
            goTo("http://localhost:9000/analysts/" + id);

            // Check we are editing the new analyst
            assertThat(title().contentEquals("New Analyst Created by Testing"));
            assertThat(find("#analystName").contains("Analyst: New Analyst Created by Testing")); // Check the main heading

            // Go to the add note page
            goTo("http://localhost:9000/analysts/" + id + "/note/new");

            // Check that the New Note page is showing with the correct analyst name
            assertThat(title().contentEquals("New Note"));
            assertThat(find("#newNote").contains("New Note")); // Check the heading
            assertThat(find("#analystName").contains("Analyst: New Analyst Created by Testing")); // Check the heading

            // Add the required fields and save
            fill("#title").with("New Subject Created by Testing");
            fill("#content").with("New Content Created by Testing");
            submit("#notesForm");

            // Check that we're on the edit analyst page and that the note was added
            assertThat(url().contentEquals("http://localhost:9000/analysts/" + id));
            assertThat(pageSource().contains("Note: New Subject Created by Testing has been created."));

            // Go to the notes page directly (html in tabs isn't available in the edit analyst page)
            goTo("http://localhost:9000/analysts/" + id + "/notes");

            // Get the id of the newly-created note
            FluentList<FluentWebElement> newNoteLinks = find(".noteId", withText("New Subject Created by Testing"));
            String noteId;
            if (newNoteLinks.size() == 1) { // There should only be one analyst with that name
                noteId = newNoteLinks.first().getId();
                goTo("http://localhost:9000/analysts/" + id + "/note/" + noteId);

                // Check we are editing the new note
                assertThat(title().contentEquals("New Subject Created by Testing"));
                assertThat(find("#noteName").contains("New Subject Created by Testing")); // Check the heading
                assertThat(find("#analystName").contains("Analyst: New Analyst Created by Testing")); // Check the heading

                // Edit the content field (with the current date and time) and save
                String currentDateTime = Utils.formatUpdatedTimestamp(Utils.getCurrentDateTime());
                fill("#content").with(currentDateTime);
                submit("#notesForm");

                // Check that we're on the edit analyst page and that the note was updated
                assertThat(url().contentEquals("http://localhost:9000/analysts/" + id));
                assertThat(pageSource().contains("Note: New Subject Created by Testing successfully updated."));

                // Check the content field was updated
                goTo("http://localhost:9000/analysts/" + id + "/note/" + noteId);
                assertThat(find("#content").contains(currentDateTime));

                // Delete the note from its edit page
                goTo("http://localhost:9000/analysts/" + id + "/note/" + noteId);
                submit("#deleteForm");

                // Check that we're on the edit analyst page and that the note is no longer in the list
                assertThat(url().contentEquals("http://localhost:9000/analysts/" + id));
                assertThat(pageSource().contains("Note: New Subject Created by Testing deleted."));
                goTo("http://localhost:9000/analysts/" + id + "/notes");
                assertThat(pageSource().contains("No notes"));
            }
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
