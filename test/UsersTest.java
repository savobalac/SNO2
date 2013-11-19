import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.libs.WS;
import play.mvc.Result;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

/**
 * Tests the Users controller (list page).
 *
 * Date: 19/11/13
 * Time: 10:20
 *
 * @author Sav Balac
 * @version %I%, %G%
 * @since 1.0
 */
public class UsersTest {


    /**
     * Starts the application.
     */
    @Before
    public void setUp() {
        start(fakeApplication());
    }


    /**
     * @verifies That the logged-in user sees the list users page (controller test).
     */
    @Test
    public void testListUsers() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                User user = User.find.byId(1L); // User 1 has username savbalac
                Result res = route(fakeRequest("GET", "/users")
                        .withSession("message", "SNO2")
                        .withSession("user", "savbalac"));
                assert(contentAsString(res).contains("Users"));
                assert(contentAsString(res).contains("<a id=\"1\" class=\"id\" href=\"/users/1\">")); // user id = 1
            }
        });
    }


    /**
     * @verifies That the real HTTP stack is running and that the list users page is displayed.
     */
    @Test
    public void testListUsersInServer() {
        running(testServer(3333), new Runnable() {
            public void run() {
                assertThat(WS.url("http://localhost:3333/users").get().get(2000).getStatus()).isEqualTo(OK); // 2000ms timeout
                assertThat(WS.url("http://localhost:3333/users").get().get(2000).getBody().contains("<h2 id=\"homeTitle\">Users"));
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
