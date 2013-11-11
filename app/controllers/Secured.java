package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.*;

import models.*;

/**
 * Ensures pages have an authenticated user.
 * Contains methods to get the username and redirect to the login page if the user is not authorised.
 *
 * Date: 18/10/13
 * Time: 11:37
 *
 * @author      Sav Balac
 * @version     %I%, %G%
 * @since       1.0
 */
public class Secured extends Security.Authenticator {


    /**
     * Gets the username.
     * @param ctx The context object
     * @return String The username
     */
    @Override
    public String getUsername(Context ctx) {
        return ctx.session().get("username");
    }


    /**
     * Goes to the login page if unauthorised.
     * @param ctx The context object
     * @return Result The login page
     */
    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(controllers.routes.Application.login());
    }


    /**
     * Checks if the logged-in user has admin rights.
     * @return boolean If the user is an admin user
     */
    public static boolean isAdminUser() {
        return User.isAdminUser(Context.current().request().username());
    }


}
