package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.*;

import models.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sav Balac
 * Date: 18/10/13
 * Time: 11:37
 * Description: Ensures pages have an authenticated user.
 * To change this template use File | Settings | File Templates.
 */
public class Secured extends Security.Authenticator {


    /**
     * Gets the username.
     * @param ctx    The context object
     * @return String    The username
     */
    @Override
    public String getUsername(Context ctx) {
        return ctx.session().get("username");
    }


    /**
     * Goes to the login page if unauthorised.
     * @param ctx    The context object
     * @return Result    The login page
     */
    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(controllers.routes.Application.login());
    }


}
