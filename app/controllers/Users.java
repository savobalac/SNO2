package controllers;

import com.avaje.ebean.Page;
import models.User;
import models.Group;
import play.data.Form;
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
 */
@Security.Authenticated(Secured.class) // All methods will require the user to be logged in
public class Users extends AbstractController {


    /**
     * Displays a paginated list of users.
     *
     * @param page          Current page number (starts from 0).
     * @param sortBy        Column to be sorted.
     * @param order         Sort order (either asc or desc).
     * @param filter        Filter applied on group name.
     * @param search        Search applied on full name.
     * @return Result  The list page.
     */
    public static Result list(int page, String sortBy, String order, String filter, String search) {

        User loggedInUser = getLoggedInUser();
        if (Secured.isAdminUser()) { // Check if an admin user

            // Return data in HTML or JSON as requested
            if (request().accepts("text/html")) {
                // Get a page of users and render the list page
                Page<User> pageUsers = User.page(page, Application.RECORDS_PER_PAGE, sortBy, order, filter, search);
                return ok(listUsers.render(pageUsers, sortBy, order, filter, search, loggedInUser));
            } else if (request().accepts("application/json") || request().accepts("text/json")) {
                return ok(User.getAllUsersAsJson());
            } else {
                return badRequest();
            }
        } else {
            return accessDenied(loggedInUser);
        }
    }


    /**
     * Show an error and the Access Denied page. In case the URL is set manually and the user doesn't have access.
     *
     * @param loggedInUser  The logged-in user.
     * @return Result  The access denied page.
     */
    private static Result accessDenied(User loggedInUser) {
        // Return data in HTML or JSON as requested
        String msg = "Only admin users can see user data.";
        if (request().accepts("text/html")) {
            flash(Utils.KEY_INFO, msg);
            return ok(accessDenied.render(loggedInUser));
        } else if (request().accepts("application/json") || request().accepts("text/json")) {
            return ok(getInfoAsJson(msg)); // Method in AbstractController
        } else {
            return badRequest();
        }
    }


    /**
     * Creates a new user.
     *
     * @return Result  The edit page.
     */
    public static Result create() {
        if (Secured.isAdminUser()) { // Check if an admin user
            return edit(0L);
        } else {
            return accessDenied(getLoggedInUser());
        }
    }


    /**
     * Displays a form to create a new or edit an existing user.
     *
     * @param id  Id of the user to edit.
     * @return Result  The edit page or the user as JSON.
     */
    public static Result edit(Long id) {

        // Check if an admin user or if editing the logged-in user
        User loggedInUser = getLoggedInUser();
        if (Secured.isAdminUser() || id.equals(loggedInUser.id)) {
            Form<User> userForm;

            // New users have id 0 and don't exist
            User user;
            if (id <= 0L) {
                user = new User();
            } else {
                // Check user exists and return if not
                user = User.find.byId(id);
                if (user == null) {
                    return noUser(id);
                }
            }
            userForm = Form.form(User.class).fill(user);

            // Return data in HTML or JSON as requested
            if (request().accepts("text/html")) {
                return ok(editUser.render(((id<0)?(0L):(id)), userForm, loggedInUser));
            } else if (request().accepts("application/json") || request().accepts("text/json")) {
                return ok(user.toJson());
            } else {
                return badRequest();
            }
        } else {
            return accessDenied(loggedInUser);
        }
    }


    /**
     * Returns either the list page or a JSON message when the user doesn't exist.
     *
     * @param  id  Id of the user.
     * @return Result  The list page or a JSON message.
     */
    private static Result noUser(Long id) {
        // Return data in HTML or JSON as requested
        if (request().accepts("text/html")) {
            return redirect(controllers.routes.Users.list(0, "fullname", "asc", "", ""));
        } else if (request().accepts("application/json") || request().accepts("text/json")) {
            return ok(getErrorAsJson("User: " + id + " does not exist."));
        } else {
            return badRequest();
        }
    }


    /**
     * Updates the user from the form.
     *
     * @param id  Id of the user to update.
     * @return Result  The list page, or the home page if updating the logged-in user.
     */
    public static Result update(Long id) {

        // Check if an admin user or if updating the logged-in user
        User loggedInUser = getLoggedInUser();
        boolean isLoggedInUser = id.equals(loggedInUser.id);
        if (Secured.isAdminUser() || isLoggedInUser) {
            Form<User> userForm = null;
            try {
                userForm = Form.form(User.class).bindFromRequest(); // Get the form data
                // Check if there are errors
                if (userForm.hasErrors()) {
                    // Return data in HTML or JSON as requested
                    if (request().accepts("text/html")) {
                        return badRequest(editUser.render(id, userForm, loggedInUser)); // Return to the editUser page
                    } else if (request().accepts("application/json") || request().accepts("text/json")) {
                        return ok(getErrorsAsJson(userForm));
                    } else {
                        return badRequest();
                    }
                } else {
                    User newUser = userForm.get(); // Get the user data
                    String confirmPassword = userForm.field("confirmPassword").value(); // Not part of the user model

                    // If a new user, check that the confirmation password has been entered (other fields have validation)
                    if (id == 0) {
                        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
                            throw new Exception("Please enter a value for the confirmation password");
                        }
                        // Check that the new and confirmation passwords are the same
                        if (!newUser.password.equals(confirmPassword)) {
                            throw new Exception("The new and confirmation passwords do not match");
                        }
                        newUser.password = Utils.hashString(newUser.password); // Hash the password
                    } else {
                        // Check user exists and return if not
                        User existingUser = User.find.byId(id);
                        if (existingUser == null) {
                            return noUser(id);
                        }
                        // Check id supplied by the form is the same as the id parameter (only possible via JSON)
                        if (!newUser.id.equals(existingUser.id)) {
                            return ok(getErrorAsJson("User id in the data (" + newUser.id + ") " +
                                                     "does not match the user id in the URL (" + id + ")."));
                        }
                        // Hash the password if the password has changed
                        if (!newUser.password.equals(existingUser.password)) {
                            newUser.password = Utils.hashString(newUser.password);
                        }
                    }

                    // Save if a new user, otherwise update, and show a message
                    newUser.saveOrUpdate();
                    String fullName = userForm.get().fullname;
                    String msg;
                    if (id == 0) {
                        msg = "User: " + fullName + " created.";
                    } else {
                        msg = "User: " + fullName + " updated.";
                    }

                    // Return data in HTML or JSON as requested
                    if (request().accepts("text/html")) {
                        // If updating the logged-in user redirect to the home page,
                        // Otherwise redirect to the list page (and remove the user from the query string)
                        flash(Utils.KEY_SUCCESS, msg);
                        if (isLoggedInUser) {
                            return redirect(controllers.routes.Application.index());
                        } else {
                            return redirect(controllers.routes.Users.list(0, "fullname", "asc", "", ""));
                        }
                    } else if (request().accepts("application/json") || request().accepts("text/json")) {
                        return ok(getSuccessAsJson(msg));
                    } else {
                        return badRequest();
                    }
                }
            } catch (Exception e) {
                // Log an error
                Utils.eHandler("Users.update(" + id + ")", e);

                // Return data in HTML or JSON as requested
                if (request().accepts("text/html")) {
                    // Show a message and return to the editUser page
                    showSaveError(e); // Method in AbstractController
                    return badRequest(editUser.render(id, userForm, loggedInUser));
                } else if (request().accepts("application/json") || request().accepts("text/json")) {
                    String msg;
                    if (id == 0) {
                        msg = "User not created.";
                    } else {
                        msg = "User: " + id + " not updated.";
                    }
                    msg += " Error: " + e.getMessage();
                    return ok(getErrorAsJson(msg));
                } else {
                    return badRequest();
                }
            }
        } else {
            return accessDenied(loggedInUser);
        }
    }


    /**
     * Deletes the user.
     *
     * @param id  Id of the user to delete.
     * @return Result  The list page.
     */
    public static Result delete(Long id) {
        if (Secured.isAdminUser()) { // Check if an admin user
            try {
                // Check user exists and return if not
                User user = User.find.byId(id);
                if (user == null) {
                    return noUser(id);
                }
                String fullName = user.fullname;

                // Delete groups
                user.delAllGroups(); // Many-many

                // Delete the user
                user.delete();
                String msg = "User: " + fullName + " deleted.";

                // Return data in HTML or JSON as requested
                if (request().accepts("text/html")) {
                    flash(Utils.KEY_SUCCESS, msg);
                    return redirect(controllers.routes.Users.list(0, "fullname", "asc", "", ""));
                } else if (request().accepts("application/json") || request().accepts("text/json")) {
                    return ok(getSuccessAsJson(msg));
                } else {
                    return badRequest();
                }
            } catch (Exception e) {
                // Log an error
                Utils.eHandler("Users.delete(" + id + ")", e);
                String msg = "User: " + id + " not deleted. Error: " + e.getMessage();
                // Return data in HTML or JSON as requested
                if (request().accepts("text/html")) {
                    showSaveError(e);
                    return redirect(controllers.routes.Users.list(0, "fullname", "asc", "", ""));
                } else if (request().accepts("application/json") || request().accepts("text/json")) {
                    return ok(getErrorAsJson(msg));
                } else {
                    return badRequest();
                }
            }
        } else {
            return accessDenied(getLoggedInUser());
        }
    }


    /**
     * Displays a (usually embedded) form to display the groups a user is assigned to.
     *
     * @param id  Id of the user.
     * @return Result  The user groups page.
     */
    public static Result editGroups(Long id) {
        if (Secured.isAdminUser()) { // Check if an admin user
            User user = User.find.byId(id);
            // Return data in HTML or JSON as requested
            if (request().accepts("text/html")) {
                return ok(tagListUserGroups.render(user)); // This template handles null users
            } else if (request().accepts("application/json") || request().accepts("text/json")) {
                if (user == null) {
                    return noUser(id);
                }
                return ok(user.getGroupsAsJson());
            } else {
                return badRequest();
            }
        } else {
            return accessDenied(getLoggedInUser());
        }
    }


    /**
     * Assigns the user to a group.
     *
     * @param id       Id of the user.
     * @param groupId  Id of the group.
     * @return Result
     */
    public static Result addGroup(Long id, Long groupId) {
        return changeGroup(id, groupId, "add");
    }


    /**
     * Deletes a group from the user.
     *
     * @param id       Id of the user.
     * @param groupId  Id of the group.
     * @return Result  If an error occurred or "OK" if successful.
     */
    public static Result delGroup(Long id, Long groupId) {
        return changeGroup(id, groupId, "delete");
    }


    /**
     * Checks if the user can update a group and calls the updateGroup method.
     *
     * @param id       Id of the user.
     * @param groupId  Id of the group.
     * @param action   "add" or "delete".
     * @return String  If an error occurred or "OK" if successful.
     */
    private static Result changeGroup(Long id, Long groupId, String action) {
        if (Secured.isAdminUser()) { // Check if an admin user
            // Return data as text or JSON as requested (browser calls use Ajax and test if "OK")
            String result = updateGroup(id, groupId, action);
            if (request().accepts("text/html")) {
                return ok(result);
            } else if (request().accepts("application/json") || request().accepts("text/json")) {
                if (result.startsWith("ERROR")) {
                    return ok(getErrorAsJson(result));
                } else {
                    return ok(getSuccessAsJson(result));
                }
            } else {
                return badRequest();
            }
        } else {
            return accessDenied(getLoggedInUser());
        }
    }


    /**
     * Adds or deletes a group to/from the user.
     *
     * @param id       Id of the user.
     * @param groupId  Id of the group.
     * @param action   "add" or "delete".
     * @return String  If an error occurred or "OK" if successful.
     */
    private static String updateGroup(Long id, Long groupId, String action) {
        User user = User.find.byId(id);
        Group group = Group.find.byId(groupId);
        try {
            if (user == null) {
                return "ERROR: User not found. Changes not saved.";
            }
            if (group == null) {
                return "ERROR: Group not found. Changes not saved.";
            } else {
                // Add, delete or return an error
                if (action.equals("add")) {
                    user.addGroup(group);
                } else if (action.equals("delete")) {
                    user.delGroup(group);
                } else {
                    return "ERROR: incorrect action, use add or delete";
                }
                if (request().accepts("text/html")) {
                    return "OK"; // "OK" is used by the calling Ajax function
                } else {
                    return "User: " + id + ", " + action + " group: " + groupId + " was successful."; // for JSON
                }
            }
        }
        catch (Exception e) {
            Utils.eHandler("Users.changeGroup(" + id + ", " + groupId + ", " + action + ")", e);
            return "ERROR: " + e.getMessage();
        }
    }


    /**
     * Displays a form to edit the user's password.
     *
     * @param id  Id of the user to edit.
     * @return Result  The edit password page.
     */
    public static Result editPassword(Long id) {
        // Check if an admin user or if editing the logged-in user
        User loggedInUser = getLoggedInUser();
        if (Secured.isAdminUser() || id.equals(loggedInUser.id)) {
            // Check user exists and return if not
            User user = User.find.byId(id);
            if (user == null) {
                return noUser(id);
            }

            // Return data in HTML or JSON as requested
            if (request().accepts("text/html")) {
                Form<User> userForm;
                userForm = Form.form(User.class).fill(user);
                return ok(editPassword.render(id, userForm, loggedInUser));
            } else if (request().accepts("application/json") || request().accepts("text/json")) {
                return ok(user.toJson());
            } else {
                return badRequest();
            }
        } else {
            return accessDenied(loggedInUser);
        }
    }


    /**
     * Updates the user's password from the form.
     *
     * @param id  Id of the user to update.
     * @return Result  The edit user page.
     */
    public static Result updatePassword(Long id) {

        // Check if an admin user or if updating the logged-in user
        User loggedInUser = getLoggedInUser();
        boolean isLoggedInUser = id.equals(loggedInUser.id);
        if (Secured.isAdminUser() || isLoggedInUser) {

            // Check user exists and return if not
            User user = User.find.byId(id);
            if (user == null) {
                return noUser(id);
            }

            Form<User> userForm = null;
            try {
                userForm = Form.form(User.class).bindFromRequest(); // Get the form data
                // Check if there are errors
                if (userForm.hasErrors()) {
                    // Return data in HTML or JSON as requested
                    if (request().accepts("text/html")) {
                        return badRequest(editPassword.render(id, userForm, loggedInUser)); // Return to editPassword
                    } else if (request().accepts("application/json") || request().accepts("text/json")) {
                        return ok(getErrorsAsJson(userForm));
                    } else {
                        return badRequest();
                    }
                } else {
                    // Get the user data
                    user = userForm.get();

                    // Check id supplied by the form is the same as the id parameter (only possible via JSON)
                    if (!user.id.equals(id)) {
                        return ok(getErrorAsJson("User id in the data (" + user.id + ") " +
                                                 "does not match the user id in the URL (" + id + ")."));
                    }

                    String newPassword = userForm.field("newPassword").value();
                    String confirmPassword = userForm.field("confirmPassword").value();

                    // Check that the new and confirmation passwords have been entered
                    if (newPassword == null || newPassword.trim().isEmpty()) {
                        throw new Exception("Please enter a value for the new password");
                    }
                    if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
                        throw new Exception("Please enter a value for the confirmation password");
                    }

                    // Check that the new and confirmation passwords are the same
                    if (!newPassword.equals(confirmPassword)) {
                        throw new Exception("The new and confirmation passwords do not match");
                    }

                    // Hash the new password
                    user.password = Utils.hashString(newPassword);

                    // Update the user
                    user.saveOrUpdate();
                    String msg = "Password updated.";

                    // Return data in HTML or JSON as requested
                    if (request().accepts("text/html")) {
                        flash(Utils.KEY_SUCCESS, msg);
                        return redirect(controllers.routes.Users.edit(id)); // Redirect to the edit user page
                    } else if (request().accepts("application/json") || request().accepts("text/json")) {
                        return ok(getSuccessAsJson(msg));
                    } else {
                        return badRequest();
                    }
                }
            } catch (Exception e) {
                // Log an error
                Utils.eHandler("Users.updatePassword(" + id + ")", e);
                String msg = "Password for user: " + id + " not updated. Error: " + e.getMessage();
                e.printStackTrace();

                // Return data in HTML or JSON as requested
                if (request().accepts("text/html")) {
                    showSaveError(e);
                    return badRequest(editPassword.render(id, userForm, loggedInUser));
                } else if (request().accepts("application/json") || request().accepts("text/json")) {
                    return ok(getErrorAsJson(msg));
                } else {
                    return badRequest();
                }
            }
        } else {
            return accessDenied(loggedInUser);
        }
    }


}
