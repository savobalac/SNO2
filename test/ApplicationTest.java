import models.Users;
import org.junit.*;
import play.libs.WS;
import play.mvc.*;
import play.test.*;
import play.libs.F.*;
import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sav Balac
 * Date: 06/11/13
 * Time: 05:47
 * Description: Tests the Application controller, login and index pages.
 * To change this template use File | Settings | File Templates.
 */
public class ApplicationTest {


    /**
     * Starts the application.
     */
    @Before
    public void setUp() {
        start(fakeApplication());
    }


    /**
     * @verifies That the login page is the first page displayed.
     */
    @Test
    public void testLoginPage() {
        running(testServer(3333, fakeApplication()), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:3333");
                assertThat(browser.pageSource()).contains("Sign in to SNO2");
            }
        });
    }


    /**
     * @verifies That the logged-in user sees the home page (controller test).
     */
    @Test
    public void testIndex() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                Users user = Users.find.byId(1L); // User 1 has username savbalac
                Result res = route(fakeRequest("GET", "/")
                                .withSession("message", "SNO2")
                                .withSession("user", "savbalac"));
                assert(contentAsString(res).contains("Donec id elit non mi porta gravida at eget metus."));
                assert(contentAsString(res).contains("savbalac"));
            }
        });
    }


    /**
     * @verifies That an incorrect route gives null.
     */
    @Test
    public void testBadRoute() {
        Result result = route(fakeRequest(GET, "/xx/Kiki"));
        assertThat(result).isNull();
    }


    /**
     * @verifies That the real HTTP stack is running and that the login page is displayed.
     */
    @Test
    public void testInServer() {
        running(testServer(3333), new Runnable() {
            public void run() {
                assertThat(WS.url("http://localhost:3333").get().get(2000).getStatus()).isEqualTo(OK); // 2000ms timeout
                assertThat(WS.url("http://localhost:3333").get().get(2000).getBody().contains("Sign in to SNO2"));
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
