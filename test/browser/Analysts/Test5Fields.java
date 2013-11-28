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
        FluentList<FluentWebElement> newAnalystLinks = find(".analystId", withText("A New Analyst, Created by Testing"));
        String id;
        if (newAnalystLinks.size() == 1) { // There should only be one analyst with that name
            id = newAnalystLinks.first().getId();

            // Admin users should find all fields visible and editable
            goTo("http://localhost:9000/analysts/" + id);

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
                System.out.println("Admin test passed.");
            } else {
                System.out.println("Admin test FAILED.");
            }

            assertTrue(findFirst("#emailAlternate").isDisplayed());
            assertTrue(findFirst("#phone").isDisplayed());

            assertTrue(findFirst("#paypalAccountEmail").isDisplayed());
            assertTrue(findFirst("#address1").isDisplayed());
            assertTrue(findFirst("#address2").isDisplayed());
            assertTrue(findFirst("#city").isDisplayed());
            assertTrue(findFirst("#state").isDisplayed());
            assertTrue(findFirst("#zip").isDisplayed());
            assertTrue(findFirst("#country").isDisplayed());

            assertTrue(findFirst("#rank_id").isEnabled()); // rank.id gets converted to rank_id by the @select helper
            assertTrue(findFirst("#emailverified").isEnabled());
            assertTrue(findFirst("#phoneVerified").isEnabled());
            assertTrue(findFirst("#contractSigned").isEnabled());
            assertTrue(findFirst("#wikiUsername").isEnabled());

            // Managers should find all fields visible and editable
            goTo("http://localhost:9000/logout");
            fill("#username").with("testmanager");
            fill("#password").with("testmanager");
            submit("#signIn");
            goTo("http://localhost:9000/analysts/" + id);

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
                System.out.println("Manager test passed.");
            } else {
                System.out.println("Manager test FAILED.");
            }

            assertTrue(findFirst("#emailAlternate").isDisplayed());
            assertTrue(findFirst("#phone").isDisplayed());

            assertTrue(findFirst("#paypalAccountEmail").isDisplayed());
            assertTrue(findFirst("#address1").isDisplayed());
            assertTrue(findFirst("#address2").isDisplayed());
            assertTrue(findFirst("#city").isDisplayed());
            assertTrue(findFirst("#state").isDisplayed());
            assertTrue(findFirst("#zip").isDisplayed());
            assertTrue(findFirst("#country").isDisplayed());

            assertTrue(findFirst("#rank_id").isEnabled());
            assertTrue(findFirst("#emailverified").isEnabled());
            assertTrue(findFirst("#phoneVerified").isEnabled());
            assertTrue(findFirst("#contractSigned").isEnabled());
            assertTrue(findFirst("#wikiUsername").isEnabled());

            // Staff members shouldn't see the Alternate email and Phone fields (but should see & edit the rest)
            goTo("http://localhost:9000/logout");
            fill("#username").with("teststaff");
            fill("#password").with("teststaff");
            submit("#signIn");
            goTo("http://localhost:9000/analysts/" + id);

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
                System.out.println("Staff test passed.");
            } else {
                System.out.println("Staff test FAILED.");
            }

            assertFalse(findFirst("#emailAlternate").isDisplayed());
            assertFalse(findFirst("#phone").isDisplayed());

            assertTrue(findFirst("#paypalAccountEmail").isDisplayed());
            assertTrue(findFirst("#address1").isDisplayed());
            assertTrue(findFirst("#address2").isDisplayed());
            assertTrue(findFirst("#city").isDisplayed());
            assertTrue(findFirst("#state").isDisplayed());
            assertTrue(findFirst("#zip").isDisplayed());
            assertTrue(findFirst("#country").isDisplayed());

            assertTrue(findFirst("#rank_id").isEnabled());
            assertTrue(findFirst("#emailverified").isEnabled());
            assertTrue(findFirst("#phoneVerified").isEnabled());
            assertTrue(findFirst("#contractSigned").isEnabled());
            assertTrue(findFirst("#wikiUsername").isEnabled());

            // Other users shouldn't see Alternate email, PayPal account email, Phone and Address fields
            goTo("http://localhost:9000/logout");
            fill("#username").with("testuser");
            fill("#password").with("testuser");
            submit("#signIn");
            goTo("http://localhost:9000/analysts/" + id);

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
                System.out.println("User test passed.");
            } else {
                System.out.println("User test FAILED.");
            }

            assertFalse(findFirst("#emailAlternate").isDisplayed());
            assertFalse(findFirst("#paypalAccountEmail").isDisplayed());
            assertFalse(findFirst("#phone").isDisplayed());
            assertFalse(findFirst("#address1").isDisplayed());
            assertFalse(findFirst("#address2").isDisplayed());
            assertFalse(findFirst("#city").isDisplayed());
            assertFalse(findFirst("#state").isDisplayed());
            assertFalse(findFirst("#zip").isDisplayed());
            assertFalse(findFirst("#country").isDisplayed());

            // Other users should find Rank, Email verified, Phone verified, Contract signed, Wiki username disabled
            assertFalse(findFirst("#rank_id").isEnabled());
            assertFalse(findFirst("#emailverified").isEnabled());
            assertFalse(findFirst("#phoneVerified").isEnabled());
            assertFalse(findFirst("#contractSigned").isEnabled());
            assertFalse(findFirst("#wikiUsername").isEnabled());
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
