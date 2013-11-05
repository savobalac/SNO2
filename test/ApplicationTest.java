import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import models.Users;
import org.junit.*;

import play.libs.WS;
import play.mvc.*;
import play.test.*;
import play.data.DynamicForm;
import play.data.validation.ValidationError;
import play.data.validation.Constraints.RequiredValidator;
import play.i18n.Lang;
import play.libs.F;
import play.libs.F.*;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;


/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {

    @Test
    public void simpleCheck() {
        int a = 1 + 1;
        assertThat(a).isEqualTo(2);
    }

    @Before
    public void setUp() {
        //start(fakeApplication(inMemoryDatabase()));
        start(fakeApplication());
    }

    @Test
    public void findById() {
        //running(fakeApplication(), new Runnable() {
            //public void run() {
                /*Computer macintosh = Computer.find.byId(21l);
                assertThat(macintosh.name).isEqualTo("Macintosh");
                assertThat(formatted(macintosh.introduced)).isEqualTo("1984-01-24");*/
                Users user = Users.find.byId(3L); // User 3 has username savbalac
                if (user != null) {
                    assertThat(user.username).isEqualTo("savbalac");
                    Content html = views.html.index.render("SNO2", user);
                    assertThat(contentType(html)).isEqualTo("text/html");
                    assertThat(contentAsString(html)).contains("savbalac");
                } else {
                    System.out.println("User, id: 3 is null");
                }
            //}
        //});
    }

    @Test
    public void renderTemplate() {
        //Content html = views.html.index.render("Your new application is ready.");
        /*Users user = Users.find.byId(3L); // User 3 has username savbalac
        Content html = views.html.index.render("SNO2", user);
        assertThat(contentType(html)).isEqualTo("text/html");
        //assertThat(contentAsString(html)).contains("Your new application is ready.");
        assertThat(contentAsString(html)).contains("savbalac");*/
    }

    @Test
    // Test the application controller's index method
    public void callIndex() {
        Users user = Users.find.byId(3L); // User 3 has username savbalac
        Result result = callAction(
                controllers.routes.ref.Application.index()
        );
        assertThat(status(result)).isEqualTo(OK);
        assertThat(contentType(result)).isEqualTo("text/html");
        assertThat(charset(result)).isEqualTo("utf-8");
        assertThat(contentAsString(result)).contains("savbalac");
    }

    @Test
    // Test the router
    public void badRoute() {
        Result result = routeAndCall(fakeRequest(GET, "/xx/Kiki"));
        assertThat(result).isNull();
    }

    @Test
    // Start a test server
    public void testInServer() {
        running(testServer(3333), new Runnable() {
            public void run() {
                assertThat(
                        WS.url("http://localhost:3333").get().get().getStatus()
                ).isEqualTo(OK);
            }
        });
    }

}
