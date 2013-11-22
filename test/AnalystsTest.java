import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.libs.WS;
import play.mvc.Result;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.fakeRequest;

/**
 * Tests the Analysts controller (list page).
 *
 * Date: 06/11/13
 * Time: 12:19
 *
 * @author      Sav Balac
 * @version     1.0
 */
public class AnalystsTest {


    /**
     * Starts the application.
     */
    @Before
    public void setUp() {
        start(fakeApplication());
    }


    /**
     * @verifies That the logged-in user sees the list analysts page (controller test).
     */
    @Test
    public void testListAnalysts() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                User user = User.find.byId(1L); // User 1 has username savbalac
                Result res = route(fakeRequest("GET", "/")
                                .withSession("message", "SNO2")
                                .withSession("user", "savbalac"));
                assert(contentAsString(res).contains("Analysts"));
                assert(contentAsString(res).contains("<a href=\"/analysts/3\">Sav Balac</a>\n")); // analyst id = 3
            }
        });
    }


    /**
     * @verifies That the real HTTP stack is running and that the list analysts page is displayed.
     */
    @Test
    public void testListAnalystsInServer() {
        running(testServer(3333), new Runnable() {
            public void run() {
                assertThat(WS.url("http://localhost:3333/analysts").get().get(2000).getStatus()).isEqualTo(OK); // 2000ms timeout
                assertThat(WS.url("http://localhost:3333/analysts").get().get(2000).getBody().contains("Analysts"));
            }
        });
    }


    /**
     * Stops the application.
     */
    @After
    public void tearDown() {
        stop(fakeApplication());
    }


}
