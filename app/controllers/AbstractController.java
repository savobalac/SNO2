package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.User;
import play.libs.Json;
import play.mvc.*;
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
     *
     * @return User  The logged-in user.
     */
    static User getLoggedInUser() {
        return User.find.where().eq("username", request().username()).findUnique();
    }


    /**
     * Shows a "Changes not saved" error.
     *
     * @param e  The exception that caused the error.
     */
    static void showSaveError(Exception e) {
        String msg = String.format("%s. Changes not saved.", e.getMessage());
        flash(Utils.FLASH_KEY_ERROR, msg);
    }


    /**
     * Gets the message as JSON.
     *
     * @param  msg  The message.
     * @return ObjectNode  The message as a JSON object node.
     */
    static ObjectNode getMessageAsJson(String msg) {
        ObjectNode result = Json.newObject();
        result.put("message", msg);
        return result;
    }


}
