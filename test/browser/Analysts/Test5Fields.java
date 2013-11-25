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
 * Tests that the correct fields are visible and editable using a browser (via Selenium and FluentLenium API).
 *
 * Date: 25/11/13
 * Time: 10:25
 *
 * @author      Sav Balac
 * @version     1.0
 */
public class Test5Fields extends FluentTest {


    /**
     * Goes to the list analyst page (via login page).
     */
    @Before
    public void setUp() {
        // Log in first
        goTo("http://localhost:9000/analysts"); // Will redirect to the login page
        fill("#username").with("savbalac"); // Admin user
        fill("#password").with("h0tsp0t");
        submit("#signIn");
    }


    /**
     * @verifies That the edit analyst page shows correct fields as visible and editable.
     */
    @Test
    public void testVisibleEditableFields() {

        // Go to the list analysts page
        goTo("http://localhost:9000/analysts");

        // Get the id of the newly-created analyst
        FluentList<FluentWebElement> newAnalystLinks = find(".analystId", withText("Created by Testing, New Analyst"));
        String id;
        if (newAnalystLinks.size() == 1) { // There should only be one analyst with that name
            id = newAnalystLinks.first().getId();

            // Admin users should find all fields visible and editable
            goTo("http://localhost:9000/analysts/" + id);
            testAdminOrManager("Admin");

            // Managers should find all fields visible and editable
            goTo("http://localhost:9000/logout");
            fill("#username").with("testmanager");
            fill("#password").with("testmanager");
            submit("#signIn");
            goTo("http://localhost:9000/analysts/" + id);
            testAdminOrManager("Manager");

            // Staff members shouldn't see the Alternate email and Phone fields (but should see & edit the rest)
            goTo("http://localhost:9000/logout");
            fill("#username").with("teststaff");
            fill("#password").with("teststaff");
            submit("#signIn");
            goTo("http://localhost:9000/analysts/" + id);

            assertThat(!findFirst("#emailAlternate").isDisplayed());
            assertThat(!findFirst("#phone").isDisplayed());

            assertThat(findFirst("#paypalAccountEmail").isDisplayed());
            assertThat(findFirst("#address1").isDisplayed());
            assertThat(findFirst("#address2").isDisplayed());
            assertThat(findFirst("#city").isDisplayed());
            assertThat(findFirst("#state").isDisplayed());
            assertThat(findFirst("#zip").isDisplayed());
            assertThat(findFirst("#country").isDisplayed());

            assertThat(findFirst("#rank_id").isEnabled());
            assertThat(findFirst("#emailverified").isEnabled());
            assertThat(findFirst("#phoneVerified").isEnabled());
            assertThat(findFirst("#contractSigned").isEnabled());
            assertThat(findFirst("#wikiUsername").isEnabled());

            // Check Alternate email and Phone are invisible and the other fields are visible and enabled
            if (!(findFirst("#emailAlternate").isDisplayed() &&
                  findFirst("#phone").isDisplayed()) &&
                findFirst("#paypalAccountEmail").isDisplayed() &&
                findFirst("#address1").isDisplayed() &&
                findFirst("#address2").isDisplayed() &&
                findFirst("#city").isDisplayed() &&
                findFirst("#state").isDisplayed() &&
                findFirst("#zip").isDisplayed() &&
                findFirst("#country").isDisplayed() &&
                findFirst("#rank_id").isEnabled() &&
                findFirst("#emailverified").isEnabled() &&
                findFirst("#phoneVerified").isEnabled() &&
                findFirst("#contractSigned").isEnabled() &&
                findFirst("#wikiUsername").isEnabled()) {
                System.out.println("Staff Test passed");
            }

            // Other users shouldn't see Alternate email, PayPal account email, Phone and Address fields
            goTo("http://localhost:9000/logout");
            fill("#username").with("testuser");
            fill("#password").with("testuser");
            submit("#signIn");
            goTo("http://localhost:9000/analysts/" + id);

            assertThat(!findFirst("#emailAlternate").isDisplayed());
            assertThat(!findFirst("#paypalAccountEmail").isDisplayed());
            assertThat(!findFirst("#phone").isDisplayed());
            assertThat(!findFirst("#address1").isDisplayed());
            assertThat(!findFirst("#address2").isDisplayed());
            assertThat(!findFirst("#city").isDisplayed());
            assertThat(!findFirst("#state").isDisplayed());
            assertThat(!findFirst("#zip").isDisplayed());
            assertThat(!findFirst("#country").isDisplayed());

            // Other users should find Rank, Email verified, Phone verified, Contract signed, Wiki username disabled
            assertThat(!findFirst("#rank_id").isEnabled());
            assertThat(!findFirst("#emailverified").isEnabled());
            assertThat(!findFirst("#phoneVerified").isEnabled());
            assertThat(!findFirst("#contractSigned").isEnabled());
            assertThat(!findFirst("#wikiUsername").isEnabled());

            // Check all relevant fields are invisible or disabled
            if (!(findFirst("#emailAlternate").isDisplayed() &&
                  findFirst("#paypalAccountEmail").isDisplayed() &&
                  findFirst("#phone").isDisplayed() &&
                  findFirst("#address1").isDisplayed() &&
                  findFirst("#address2").isDisplayed() &&
                  findFirst("#city").isDisplayed() &&
                  findFirst("#state").isDisplayed() &&
                  findFirst("#zip").isDisplayed() &&
                  findFirst("#country").isDisplayed() &&
                  findFirst("#rank_id").isEnabled() &&
                  findFirst("#emailverified").isEnabled() &&
                  findFirst("#phoneVerified").isEnabled() &&
                  findFirst("#contractSigned").isEnabled() &&
                  findFirst("#wikiUsername").isEnabled())) {
                System.out.println("User Test passed");
            }
        }
    }


    /**
     * Tests that all relevant fields are visible or enabled.
     * @param group  The user's group, e.g. Admin or Manager
     */
    private void testAdminOrManager(String group) {

        // Admin users or managers should find all fields visible and editable
        assertThat(findFirst("#emailAlternate").isDisplayed());
        assertThat(findFirst("#phone").isDisplayed());

        assertThat(findFirst("#paypalAccountEmail").isDisplayed());
        assertThat(findFirst("#address1").isDisplayed());
        assertThat(findFirst("#address2").isDisplayed());
        assertThat(findFirst("#city").isDisplayed());
        assertThat(findFirst("#state").isDisplayed());
        assertThat(findFirst("#zip").isDisplayed());
        assertThat(findFirst("#country").isDisplayed());

        assertThat(findFirst("#rank_id").isEnabled()); // rank.id gets converted to rank_id by the @select helper
        assertThat(findFirst("#emailverified").isEnabled());
        assertThat(findFirst("#phoneVerified").isEnabled());
        assertThat(findFirst("#contractSigned").isEnabled());
        assertThat(findFirst("#wikiUsername").isEnabled());

        // Check all relevant fields are visible and enabled
        if (findFirst("#emailAlternate").isDisplayed() &&
            findFirst("#phone").isDisplayed() &&
            findFirst("#paypalAccountEmail").isDisplayed() &&
            findFirst("#address1").isDisplayed() &&
            findFirst("#address2").isDisplayed() &&
            findFirst("#city").isDisplayed() &&
            findFirst("#state").isDisplayed() &&
            findFirst("#zip").isDisplayed() &&
            findFirst("#country").isDisplayed() &&
            findFirst("#rank_id").isEnabled() &&
            findFirst("#emailverified").isEnabled() &&
            findFirst("#phoneVerified").isEnabled() &&
            findFirst("#contractSigned").isEnabled() &&
            findFirst("#wikiUsername").isEnabled()) {
            System.out.println(group + " Test passed");
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
