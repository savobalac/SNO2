package controllers;

import models.Users;
import java.security.NoSuchAlgorithmException;
import play.api.mvc.Call;
import play.mvc.Controller;
import play.mvc.Security;

import play.mvc.Result;
import play.data.*;
import static play.data.Form.*;
import views.html.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sav Balac
 * Date: 16/10/13
 * Time: 12:59
 * Description: Application controller that renders pages and logs the user in and out.
 * To change this template use File | Settings | File Templates.
 */
public class Application extends Controller {

    // Constants
    public static final int RECORDS_PER_PAGE = 20;

    // Add a constant here when creating a new list page
    public static final int PAGE_TYPE_ANALYSTS =  1;


    /**
     * Returns the index page.
     * @return Result
     */
    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(index.render("SNO2", Users.find.where().eq("username", request().username()).findUnique() ));
    }


    /**
     * Returns the login page.
     * @return Result
     */
    public static Result login() {
        return ok(login.render(form(Login.class)));
    }


    /**
     * Authenticates the user and goes the index page.
     * @return Result
     */
    public static Result authenticate() {
        Form<Login> loginForm = form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session().clear();
            session("username", loginForm.get().username);
            return redirect(controllers.routes.Application.index());
        }
    }


    /**
     * Logs the user out and goes to the login page.
     * @return Result
     */
    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(controllers.routes.Application.login());
    }


    /**
     * When a (pagination) link is clicked, return the required list page
     * @param pageType           The type of page, e.g. Analysts
     * @param page               The current page
     * @param sortBy             The sort column
     * @param sortOrder          Ascending or descending
     * @param filter1            The first search filter
     * @param filter2            The second search filter
     * @param filterNum          A numeric search filter
     * @return Call
     */
    public static Call getLink(int pageType, int page, String sortBy, String sortOrder,
                                            String filter1, String filter2, Long filterNum) {

        // Return the list page depending on the page type
        switch (pageType) {
            case PAGE_TYPE_ANALYSTS:
                return controllers.routes.Analysts.list(page, sortBy, sortOrder, filter1, filter2);
            default:
                return null;
        }

    }


    /**
     * Inner class that hold the username and password and validates the user.
     */
    public static class Login {

        public String username;
        public String password;

        /**
         * Calls a method to authenticate the user.
         * @return String    An error message if not authenticated
         */
        public String validate() throws NoSuchAlgorithmException {
            if (Users.authenticate(username, password) == null) {
                return "Invalid username or password";
            }
            return null;
        }

    }


}
