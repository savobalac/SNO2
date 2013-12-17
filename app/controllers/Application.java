package controllers;

import models.User;
import java.security.NoSuchAlgorithmException;
import play.api.mvc.Call;
import play.mvc.Security;

import play.mvc.Result;
import play.data.*;
import static play.data.Form.*;
import utils.Utils;
import views.html.*;

/**
 * Application controller that renders the home (index) page and logs the user in and out.
 * Contains a method to call the correct list page when a pagination link is used, and an inner class for login.
 *
 * Date: 16/10/13
 * Time: 12:59
 *
 * @author      Sav Balac
 * @version     1.1
 */
public class Application extends AbstractController {

    // Constants
    public  static final int RECORDS_PER_PAGE = 10;
    private static final String AUTHENTICATION_ERROR_MSG = "Invalid username or password.";

    // Add a constant here when creating a new list page
    public static final int PAGE_TYPE_ANALYSTS  =  1;
    public static final int PAGE_TYPE_USERS     =  2;


    /**
     * Renders the index page.
     *
     * @return Result  The home page.
     */
    @Security.Authenticated(Secured.class) // Will require the user to be logged in
    public static Result index() {
        // Return data in HTML or JSON as requested
        if (request().accepts("text/html")) {
            return ok(index.render("SNO2", User.find.where().eq("username", request().username()).findUnique()) );
        } else if (request().accepts("application/json") || request().accepts("text/json")) {
            return ok(getSuccessAsJson("Home page."));
        } else {
            return badRequest();
        }
    }


    /**
     * Renders the login page.
     *
     * @return Result  The login page.
     */
    public static Result login() {
        // Return data in HTML or JSON as requested
        if (request().accepts("text/html")) {
            return ok(login.render(form(Login.class)));
        } else if (request().accepts("application/json") || request().accepts("text/json")) {
            return ok(getInfoAsJson("Please sign in with a valid username and password."));
        } else {
            return badRequest();
        }
    }


    /**
     * Authenticates the user and goes the index page.
     *
     * @return Result  The home page if logged in.
     */
    public static Result authenticate() {
        Form<Login> loginForm = form(Login.class).bindFromRequest(); // Get the form data
        // Check if there are errors
        if (loginForm.hasErrors()) {
            // Return data in HTML or JSON as requested
            if (request().accepts("text/html")) {
                return badRequest(login.render(loginForm));
            } else if (request().accepts("application/json") || request().accepts("text/json")) {
                return ok(getErrorAsJson(AUTHENTICATION_ERROR_MSG));
            } else {
                return badRequest();
            }
        } else {
            session().clear();
            String username = loginForm.get().username;
            session("username", username);
            return redirect(controllers.routes.Application.index());
        }
    }


    /**
     * Logs the user out and goes to the login page.
     *
     * @return Result  The login page.
     */
    public static Result logout() {
        session().clear();
        // Return data in HTML or JSON as requested
        String msg = "You have signed out.";
        if (request().accepts("text/html")) {
            flash(Utils.KEY_SUCCESS, msg);
            return redirect(controllers.routes.Application.login());
        } else if (request().accepts("application/json") || request().accepts("text/json")) {
            return ok(getSuccessAsJson(msg));
        } else {
            return badRequest();
        }
    }


    /**
     * When a (pagination) link is clicked, return the required list page.
     *
     * @param pageType           The type of page, e.g. Analysts.
     * @param page               The current page.
     * @param sortBy             The sort column.
     * @param sortOrder          Ascending or descending.
     * @param filter1            The first search filter.
     * @param filter2            The second search filter.
     * @param filterNum          A numeric search filter.
     * @return Call  The requested list page.
     */
    public static Call getLink(int pageType, int page, String sortBy, String sortOrder,
                                            String filter1, String filter2, Long filterNum) {

        // Return the list page depending on the page type
        switch (pageType) {
            case PAGE_TYPE_ANALYSTS:
                return controllers.routes.Analysts.list(page, sortBy, sortOrder, filter1, filter2);
            case PAGE_TYPE_USERS:
                return controllers.routes.Users.list(page, sortBy, sortOrder, filter1, filter2);
            default:
                return null;
        }

    }


    /**
     * Inner class that holds the username and password and validates the user.
     */
    public static class Login {

        public String username;
        public String password;

        /**
         * Calls a method to authenticate the user.
         *
         * @return String                       An error message if not authenticated, else null.
         * @throws NoSuchAlgorithmException     If the algorithm doesn't exist.
         */
        public String validate() throws NoSuchAlgorithmException {
            if (username == null || password == null || User.authenticate(username, password) == null) {
                return AUTHENTICATION_ERROR_MSG;
            }
            return null;
        }

    }


}
