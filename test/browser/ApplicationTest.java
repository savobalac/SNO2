package browser;

import org.fluentlenium.adapter.FluentTest;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: Sav Balac
 * Date: 06/11/13
 * Time: 11:15
 * Description: Tests the login and index pages using a browser (via Selenium and FluentLenium API).
 * To change this template use File | Settings | File Templates.
 */
public class ApplicationTest extends FluentTest {


    /**
     * @verifies That signing in sends the user to the home page.
     */
    @Test
    public void testLoginToHomePage() {
        goTo("http://localhost:9000/");
        fill("#username").with("savbalac");
        fill("#password").with("h0tsp0t");
        submit("#signIn");
        assertThat(url().contentEquals("http://localhost:9000/")); // The home (index) page
        assertThat(title().contentEquals("SNO2"));
        assertThat(pageSource().contains("Donec id elit non mi porta gravida at eget metus."));
    }


}
