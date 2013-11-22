import org.junit.*;
import play.test.*;
import play.libs.F.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;


/**
 * Integration test that just starts the application and checks the sign in page is showing.
 *
 * Date: 06/11/13
 * Time: 05:47
 *
 * @author      Sav Balac
 * @version     1.0
 */
public class IntegrationTest {


    /**
     * @verifies That the application has started.
     */
    @Before
    public void setUp() {
        start(fakeApplication());
    }


    /**
     * @verifies That the sign in page is current.
     */
    @Test
    public void testSignIn() {
        running(testServer(3333, fakeApplication()), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:3333");
                assertThat(browser.pageSource()).contains("Sign in to SNO2");
            }
        });
    }


    /**
     * @verifies That the application has stopped.
     */
    @After
    public void tearDown() {
        stop(fakeApplication());
    }


}
