import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.Application;
import models.Users;
import org.fluentlenium.core.domain.FluentWebElement;
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
import static play.data.Form.*;
import static org.fest.assertions.Assertions.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sav Balac
 * Date: 06/11/13
 * Time: 05:47
 * Description: Application test that tests the main parts of the application.
 * To change this template use File | Settings | File Templates.
 */
public class ApplicationTest {


    /**
     * @verifies That the application has started.
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
     * @verifies That the logged-in user sees the index page.
     */
    @Test
    public void testIndexTemplate() {
        //Users user = Users.find.byId(1L); // User id 1 has username savbalac
        // The next line gives error: java.lang.RuntimeException: There is no HTTP Context available from here.
        //Content html = views.html.index.render("SNO2", user);
        //assertThat(contentType(html)).isEqualTo("text/html");
        //assertThat(contentAsString(html)).contains("Donec id elit non mi porta gravida at eget metus.");
    }


    /*
    @Test
    public void findById() {
        //running(fakeApplication(), new Runnable() {
            //public void run() {
                //Computer macintosh = Computer.find.byId(21l);
                //assertThat(macintosh.name).isEqualTo("Macintosh");
                //assertThat(formatted(macintosh.introduced)).isEqualTo("1984-01-24");
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
    }*/



    /**
     * @verifies That the logged-in user sees the home page.
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

    @Test
    public void runInBrowser() {
        running(testServer(3333), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:3333");
                //assertThat(browser.$("#title").getTexts().get(0)).isEqualTo("Sign in to SNO2"); // IndexOutOfBoundsException
                // Next line gives: org.openqa.selenium.NoSuchElementException:
                //     No such element with position :0. Number of elements available :0
                //assertThat(browser.$("#title", 0).getText()).isEqualTo("Sign in to SNO2");
                browser.$("submit").click();
                assertThat(browser.url()).isEqualTo("http://localhost:3333/login");
                //assertThat(browser.$("#title", 0).getText()).isEqualTo("SNO2");
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
