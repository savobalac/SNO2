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
import views.html.*;

/**
 * Users controller with methods to list, create, edit, update and delete.
 * There are methods to list and modify a user's groups, show the edit password page and update.
 * Non-admin users can only update their details and password.
 *
 * Date:        08/11/13
 * Time:        12:14
 *
 * @author      Sav Balac
 * @version     1.0
 * @since       1.0
 */
@Security.Authenticated(Secured.class) // All methods will require the user to be logged in
public class Users extends AbstractController {


    /**
     * Displays a paginated list of users.
     *
     * @param page          Current page number (starts from 0)
     * @param sortBy        Column to be sorted
     * @param order         Sort order (either asc or desc)
     * @param filter        Filter applied on group name
     * @param search        Search applied on full name
     * @return Result
     */
    public static Result list(int page, String sortBy, String order, String filter, String search) {

        // Get a page of users and render the list page
        User loggedInUser = getLoggedInUser();
        if (Secured.isAdminUser()) { // Check if an admin user
            Page<User> pageUsers = User.page(page, Application.RECORDS_PER_PAGE, sortBy, order, filter, search);
            return ok(listUsers.render(pageUsers, sortBy, order, filter, search, loggedInUser));
        } else {
            return accessDenied(loggedInUser);
        }
    }


    /**
     * Show an error and the Access Denied page. In case the URL is set manually and the user doesn't have access.
     *
     * @param loggedInUser  The logged-in user
     * @return Result
     */
    private static Result accessDenied(User loggedInUser) {
        flash(Utils.FLASH_KEY_INFO, "Only admin users can see user pages.");
        return ok(accessDenied.render(loggedInUser));
    }


    /**
     * Creates a new user.
     * @return Result
     */
    public static Result create() {
        if (Secured.isAdminUser()) { // Check if an admin user
            return edit(new Long(0));
        } else {
            return accessDenied(getLoggedInUser());
        }
    }


    /**
     * Displays a form to create a new or edit an existing user.
     * @param id Id of the user to edit
     * @return Result
     */
    public static Result edit(Long id) {

        // Check if an admin user or if editing the logged-in user
        User loggedInUser = getLoggedInUser();
        if (Secured.isAdminUser() || id.equals(loggedInUser.id)) {
            Form<User> userForm;
            // New users have id = 0
            if (id <= 0L) {
                userForm = Form.form(User.class).fill(new User());
            } else {
                userForm = Form.form(User.class).fill(User.find.byId(id));
            }
            return ok(editUser.render(((id<0)?(new Long(0)):(id)), userForm, loggedInUser));
        } else {
            return accessDenied(loggedInUser);
        }
    }


    /**
     * Updates the user from the form.
     * @param id Id of the user to update
     * @return Result
     */
    public static Result update(Long id) {

        // Check if an admin user or if updating the logged-in user
        User loggedInUser = getLoggedInUser();
        boolean isLoggedInUser = id.equals(loggedInUser.id);
        if (Secured.isAdminUser() || isLoggedInUser) {
            Form<User> userForm = Form.form(User.class).bindFromRequest(); // Get the form data
            try {
                if (userForm.hasErrors()) { // Return to the editUser page
                    return badRequest(editUser.render(id, userForm, loggedInUser));
                } else {
                    User newUser = userForm.get(); // Get the user data
                    String confirmPassword = userForm.field("confirmPassword").value(); // Not part of the user model

                    // If a new user, check that the confirmation password has been entered (other fields have validation)
                    if (id == 0) {
                        if (confirmPassword.trim().isEmpty()) {
                            throw new Exception("Please enter a value for the confirmation password");
                        }
                        // Check that the new and confirmation passwords are the same
                        if (!newUser.password.equals(confirmPassword)) {
                            throw new Exception("The new and confirmation passwords do not match");
                        }
                    }

                    // Hash the password when creating a new user or if the password has changed
                    User oldUser = User.find.byId(id);
                    if (id == 0 || (!newUser.password.equals(oldUser.password))) {
                        newUser.password = Utils.hashString(newUser.password);
                    }

                    // Save if a new user, otherwise update, and show a message
                    newUser.saveOrUpdate();
                    String fullName = userForm.get().fullname;
                    String msg;
                    if (id == 0) {
                        msg = "User: " + fullName + " has been created.";
                    } else {
                        msg = "User: " + fullName + " successfully updated.";
                    }
                    flash(Utils.FLASH_KEY_SUCCESS, msg);

                    // If updating the logged-in user redirect to the home page,
                    // Otherwise redirect to the list page (and remove the user from the query string)
                    if (isLoggedInUser) {
                        return redirect(controllers.routes.Application.index());
                    } else {
                        return redirect(controllers.routes.Users.list(0, "fullname", "asc", "", ""));
                    }
                }
            } catch (Exception e) {
                // Log an error, show a message and return to the editUser page
                Utils.eHandler("Users.update(" + id + ")", e);
                showSaveError(e); // Method in AbstractController
                return badRequest(editUser.render(id, userForm, loggedInUser));
            }
        } else {
            return accessDenied(loggedInUser);
        }
    }


    /**
     * Deletes the user.
     * @param id Id of the user to delete
     * @return Result
     */
    public static Result delete(Long id) {
        if (Secured.isAdminUser()) { // Check if an admin user
            try {
                // Find the user record
                User user = User.find.byId(id);
                String fullName = user.fullname;

                // Delete groups
                user.delAllGroups(); // Many-many

                // Delete the user and show a message
                user.delete();
                flash(Utils.FLASH_KEY_SUCCESS, "User: " + fullName + " deleted.");
            } catch (Exception e) {
                // Log an error and show a message
                Utils.eHandler("Users.delete(" + id + ")", e);
                showSaveError(e); // Method in AbstractController
            } finally {
                // Redirect to remove user from query string
                return redirect(controllers.routes.Users.list(0, "fullname", "asc", "", ""));
            }
        } else {
            return accessDenied(getLoggedInUser());
        }
    }


    /**
     * Displays a (usually embedded) form to display the groups a user is assigned to.
     * @param id Id of the user
     * @return Result
     */
    public static Result editGroups(Long id) {
        if (Secured.isAdminUser()) { // Check if an admin user
            User user = User.find.byId(id);
            return ok(tagListUserGroups.render(user));
        } else {
            return accessDenied(getLoggedInUser());
        }
    }


    /**
     * Assigns the user to a group.
     * @param id       Id of the user
     * @param groupId  Id of the group
     * @return Result
     */
    public static Result addGroup(Long id, Long groupId) {
        return changeGroup(id, groupId, "add");
    }


    /**
     * Deletes a group from the user.
     * @param id       Id of the user
     * @param groupId  Id of the group
     * @return Result
     */
    public static Result delGroup(Long id, Long groupId) {
        return changeGroup(id, groupId, "delete");
    }


    /**
     * Adds or deletes a group to/from the user.
     * @param id       Id of the user
     * @param groupId  Id of the group
     * @param action   "add" or "delete"
     * @return Result
     */
    private static Result changeGroup(Long id, Long groupId, String action) {
        if (Secured.isAdminUser()) { // Check if an admin user
            User user = User.find.byId(id);
            Group group = Group.find.byId(groupId);
            try {
                if (user == null) {
                    return ok("ERROR: User not found. Changes not saved.");
                }
                if (group == null) {
                    return ok("ERROR: Group not found. Changes not saved.");
                } else {
                    // Add, delete or return an error
                    if (action.equals("add")) {
                        user.addGroup(group);
                    } else if (action.equals("delete")) {
                        user.delGroup(group);
                    } else {
                        return ok("ERROR: incorrect action, use add or delete");
                    }
                    return ok("OK"); // "OK" is used by the calling Ajax function
                }
            }
            catch (Exception e) {
                Utils.eHandler("Users.changeGroup(" + id + ", " + groupId + ", " + action + ")", e);
                return ok("ERROR: " + e.getMessage());
            }
        } else {
            return accessDenied(getLoggedInUser());
        }
    }


    /**
     * Displays a form to edit the user's password.
     * @param id Id of the user to edit
     * @return Result
     */
    public static Result editPassword(Long id) {
        // Check if an admin user or if editing the logged-in user
        User loggedInUser = getLoggedInUser();
        if (Secured.isAdminUser() || id.equals(loggedInUser.id)) {
            Form<User> userForm;
            userForm = Form.form(User.class).fill(User.find.byId(id));
            return ok(editPassword.render(id, userForm, loggedInUser));
        } else {
            return accessDenied(loggedInUser);
        }
    }


    /**
     * Updates the user's password from the form.
     * @param id Id of the user to update
     * @return Result
     */
    public static Result updatePassword(Long id) {

        // Check if an admin user or if updating the logged-in user
        User loggedInUser = getLoggedInUser();
        boolean isLoggedInUser = id.equals(loggedInUser.id);
        if (Secured.isAdminUser() || isLoggedInUser) {
            Form<User> userForm = Form.form(User.class).bindFromRequest(); // Get the form data
            try {
                if (userForm.hasErrors()) { // Return to the editPassword page
                    return badRequest(editPassword.render(id, userForm, loggedInUser));
                } else {
                    // Get the user data
                    User user = userForm.get();

                    String newPassword = userForm.field("newPassword").value();
                    String confirmPassword = userForm.field("confirmPassword").value();

                    // Check that the new and confirmation passwords have been entered
                    if (newPassword.trim().isEmpty()) {
                        throw new Exception("Please enter a value for the new password");
                    }
                    if (confirmPassword.trim().isEmpty()) {
                        throw new Exception("Please enter a value for the confirmation password");
                    }

                    // Check that the new and confirmation passwords are the same
                    if (!newPassword.equals(confirmPassword)) {
                        throw new Exception("The new and confirmation passwords do not match");
                    }

                    // Hash the new password
                    user.password = Utils.hashString(newPassword);

                    // Update the user and show a message
                    user.saveOrUpdate();
                    flash(Utils.FLASH_KEY_SUCCESS, "Password successfully updated.");

                    // Redirect to the edit user page
                    return redirect(controllers.routes.Users.edit(id));
                }
            } catch (Exception e) {
                // Log an error, show a message and return to the editPassword page
                Utils.eHandler("Users.updatePassword(" + id + ")", e);
                showSaveError(e); // Method in AbstractController
                return badRequest(editPassword.render(id, userForm, loggedInUser));
            }
        } else {
            return accessDenied(loggedInUser);
        }
    }
}
