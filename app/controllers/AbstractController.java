package controllers;

import models.User;
import play.mvc.Controller;
import utils.Utils;

/**
 * Top-level controller providing common fields and methods.
 *
 * Date:        21/11/13
 * Time:        12:56
 *
 * @author      Sav Balac
 * @version     1.0
 */
public abstract class AbstractController extends Controller {


    /**
     * Returns the logged-in user.
     * @return User
     */
    static User getLoggedInUser() {
        return User.find.where().eq("username", request().username()).findUnique();
    }


    /**
     * Shows a "Changes not saved" error.
     *
     * @param e  The exception that caused the error
     */
    static void showSaveError(Exception e) {
        String msg = String.format("%s. Changes not saved.", e.getMessage());
        flash(Utils.FLASH_KEY_ERROR, msg);
    }


}
