package controllers;

import play.mvc.*;
import play.mvc.Http.*;

import models.*;
import utils.Utils;

import java.security.NoSuchAlgorithmException;

/**
 * Ensures pages have an authenticated user.
 * Contains methods to get the username, redirect to the login page if not authorised and check if an admin user.
 *
 * Date: 18/10/13
 * Time: 11:37
 *
 * @author      Sav Balac
 * @version     1.1
 */
public class Secured extends Security.Authenticator {


    /**
     * Gets the username.
     * @param ctx The context object
     * @return String The username
     */
    @Override
    public String getUsername(Context ctx) {

        // Get the username from the cookie
        // Requests from the JSON API may include the username and password to avoid logging in first
        if (ctx.session().get("username") != null) {
            return ctx.session().get("username");
        } else {
            if (ctx.request().accepts("application/json") || ctx.request().accepts("text/json")) {
                String username = ctx.request().getHeader("username");
                String password = ctx.request().getHeader("password");
                Application.Login login = new Application.Login();
                login.username = username;
                login.password = password;
                try {
                    if (login.validate() == null) {
                        return username;
                    } else {
                        return null;
                    }
                } catch (NoSuchAlgorithmException ex) {
                    // Log an error
                    Utils.eHandler("Secured.getUsername()", ex);
                    return null;
                }
            } else {
                return null;
            }
        }
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
