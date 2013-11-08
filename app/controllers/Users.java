package controllers;

import com.avaje.ebean.Page;
import models.User;
import models.Group;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.Utils;
import views.html.Users.*;

/**
 * Users controller with methods to list, create, edit, update and delete.
 * There are also methods to render and modify a user's groups.
 *
 * Date:        08/11/13
 * Time:        12:14
 *
 * @author      Sav Balac
 * @version     %I%, %G%
 * @since       1.0
 */
@Security.Authenticated(Secured.class) // All methods will require the user to be logged in
public class Users extends Controller {


    /**
     * Displays a paginated list of users.
     *
     * @param page          Current page number (starts from 0)
     * @param sortBy        Column to be sorted
     * @param order         Sort order (either asc or desc)
     * @param filter        Filter applied on <column name>
     * @param search        Filter applied on <column names>
     * @return Result
     */
    public static Result list(int page, String sortBy, String order, String filter, String search) {
        // Get a page of users and render the list page
        Page<User> pageUsers = User.page(page, Application.RECORDS_PER_PAGE, sortBy, order, filter, search);
        User user = User.find.where().eq("username", request().username()).findUnique();
        return ok(listUsers.render(pageUsers, sortBy, order, filter, search, user));
    }


    /**
     * Creates a new user.
     * @return Result
     */
    public static Result create() {
        return edit(new Long(0));
    }


    /**
     * Displays a form to create a new or edit an existing user.
     * @param id Id of the user to edit
     * @return Result
     */
    public static Result edit(Long id) {
        Form<User> userForm;
        // New users have id = 0
        if (id <= 0L) {
            userForm = Form.form(User.class).fill(new User());
        }
        else {
            User user = User.find.byId(id);
            userForm = Form.form(User.class).fill(User.find.byId(id));
        }
        User user = User.find.where().eq("username", request().username()).findUnique();
        return ok(editUser.render(((id<0)?(new Long(0)):(id)), userForm, user));
    }


    /**
     * Updates the user from the form.
     * @param id Id of the user to update
     * @return Result
     */
    public static Result update(Long id) {
        // Get the user form and user
        Form<User> userForm = Form.form(User.class).bindFromRequest();
        User user = User.find.where().eq("username", request().username()).findUnique();
        String msg;
        try {
            if (userForm.hasErrors()) { // Return to the editUser page
                return badRequest(editUser.render(id, userForm, user));
            } else {
                User a = userForm.get(); // Get the user data

                // Save if a new user, otherwise update, and show a message
                a.saveOrUpdate();
                String fullName = userForm.get().fullname;
                if (id==null || id==0) {
                    msg = "User " + fullName + " has been created.";
                    flash(Utils.FLASH_KEY_SUCCESS, msg);
                } else {
                    msg = "User " + fullName + " successfully updated.";
                    flash(Utils.FLASH_KEY_SUCCESS, msg);
                }
                // Redirect to remove user from query string
                return redirect(controllers.routes.Users.list(0, "fullname", "asc", "", ""));
            }
        } catch (Exception e) {
            // Log an error, show a message and return to the editUser page
            Utils.eHandler("Users.update(" + id.toString() + ")", e);
            msg = String.format("Changes not saved. Error encountered ( %s ).", e.getMessage());
            flash(Utils.FLASH_KEY_ERROR, msg);
            return badRequest(editUser.render(id, userForm, user));
        }
    }


    /**
     * Deletes the user.
     * @param id Id of the user to delete
     * @return Result
     */
    public static Result delete(Long id) {
        String msg;
        try {
            // Find the user record
            User user = User.find.byId(id);

            // Delete desks
            user.delAllGroups(); // Many-many

            // Delete the user
            user.delete();
            msg = "User deleted.";
            flash(Utils.FLASH_KEY_SUCCESS, msg);
        } catch (Exception e) {
            // Log an error and show a message
            Utils.eHandler("Users.delete(" + id.toString() + ")", e);
            msg = String.format("Error encountered (%s).", e.getMessage());
            flash(Utils.FLASH_KEY_ERROR, msg);
        } finally {
            // Redirect to remove user from query string
            return redirect(controllers.routes.Users.list(0, "fullname", "asc", "", ""));
        }
    }


    /**
     * Displays a (usually embedded) form to display the groups a user is assigned to.
     * @param id Id of the user
     * @return Result
     */
    public static Result editGroups(Long id) {
        User user = User.find.byId(id);
        return ok(tagListUserGroups.render(user));
    }


    /**
     * Assigns the user to a group.
     * @param id       Id of the user
     * @param groupId  Id of the group
     * @return Result
     */
    public static Result addGroup(Long id, Long groupId) {
        User user = User.find.byId(id);
        Group group = Group.find.byId(groupId);
        try {
            if(user == null) {
                return ok("Error: User not found.");
            }
            else {
                user.addGroup(group);
                return ok("OK"); // "OK" is used by the calling Ajax function
            }
        }
        catch (Exception e) {
            Utils.eHandler("Users.addGroup()", e);
            return ok(e.getMessage());
        }
    }


    /**
     * Deletes a group from the user.
     * @param id       Id of the user
     * @param groupId  Id of the group
     * @return Result
     */
    public static Result delGroup(Long id, Long groupId) {
        User user = User.find.byId(id);
        Group group = Group.find.byId(groupId);
        try {
            if (user == null) {
                return ok("Error: User not found.");
            }
            if (group == null) {
                return ok("Error: Group not found.");
            } else {
                user.delGroup(group);
                return ok("OK"); // "OK" is used by the calling Ajax function
            }
        }
        catch (Exception e) {
            Utils.eHandler("Users.delGroup()", e);
            return ok(e.getMessage());
        }
    }




}
