package controllers;

import com.avaje.ebean.Page;
import models.Group;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.Utils;
import views.html.Groups.*;

/**
 * Groups controller with methods to list, create, edit, update and delete.
 *
 * Date:        08/11/13
 * Time:        13:52
 *
 * @author      Sav Balac
 * @version     %I%, %G%
 * @since       1.0
 */
@Security.Authenticated(Secured.class) // All methods will require the user to be logged in
public class Groups extends Controller {


    /**
     * Displays a paginated list of groups.
     *
     * @param page          Current page number (starts from 0)
     * @param sortBy        Column to be sorted
     * @param order         Sort order (either asc or desc)
     * @param filter        Filter applied on <column name>
     * @param search        Filter applied on <column names>
     * @return Result
     */
    public static Result list(int page, String sortBy, String order, String filter, String search) {
        // Get a page of groups and render the list page
        Page<Group> pageGroups = Group.page(page, Application.RECORDS_PER_PAGE, sortBy, order, filter, search);
        User user = User.find.where().eq("username", request().username()).findUnique();
        return ok(listGroups.render(pageGroups, sortBy, order, filter, search, user));
    }


    /**
     * Creates a new group.
     * @return Result
     */
    public static Result create() {
        return edit(new Long(0));
    }


    /**
     * Displays a form to create a new or edit an existing group.
     * @param id Id of the group to edit
     * @return Result
     */
    public static Result edit(Long id) {
        Form<Group> groupForm;
        // New groups have id = 0
        if (id <= 0L) {
            groupForm = Form.form(Group.class).fill(new Group());
        }
        else {
            Group group = Group.find.byId(id);
            groupForm = Form.form(Group.class).fill(Group.find.byId(id));
        }
        User user = User.find.where().eq("username", request().username()).findUnique();
        return ok(editGroup.render(((id<0)?(new Long(0)):(id)), groupForm, user));
    }


    /**
     * Updates the group from the form.
     * @param id Id of the group to update
     * @return Result
     */
    public static Result update(Long id) {
        // Get the group form and user
        Form<Group> groupForm = Form.form(Group.class).bindFromRequest();
        User user = User.find.where().eq("username", request().username()).findUnique();
        String msg;
        try {
            if (groupForm.hasErrors()) { // Return to the editGroup page
                return badRequest(editGroup.render(id, groupForm, user));
            } else {
                Group group = groupForm.get(); // Get the group data

                // Save if a new group, otherwise update, and show a message
                group.saveOrUpdate();
                String name = groupForm.get().name;
                if (id==null || id==0) {
                    msg = "Group " + name + " has been created.";
                    flash(Utils.FLASH_KEY_SUCCESS, msg);
                } else {
                    msg = "Group " + name + " successfully updated.";
                    flash(Utils.FLASH_KEY_SUCCESS, msg);
                }
                // Redirect to remove group from query string
                return redirect(controllers.routes.Groups.list(0, "name", "asc", "", ""));
            }
        } catch (Exception e) {
            // Log an error, show a message and return to the editGroup page
            Utils.eHandler("Groups.update(" + id.toString() + ")", e);
            msg = String.format("Changes not saved. Error encountered ( %s ).", e.getMessage());
            flash(Utils.FLASH_KEY_ERROR, msg);
            return badRequest(editGroup.render(id, groupForm, user));
        }
    }


    /**
     * Deletes the group.
     * @param id Id of the group to delete
     * @return Result
     */
    public static Result delete(Long id) {
        String msg;
        try {
            // Find the group record
            Group group = Group.find.byId(id);

            // Delete the group
            group.delete();
            msg = "Group deleted.";
            flash(Utils.FLASH_KEY_SUCCESS, msg);
        } catch (Exception e) {
            // Log an error and show a message
            Utils.eHandler("Groups.delete(" + id.toString() + ")", e);
            msg = String.format("Error encountered (%s).", e.getMessage());
            flash(Utils.FLASH_KEY_ERROR, msg);
        } finally {
            // Redirect to remove group from query string
            return redirect(controllers.routes.Groups.list(0, "name", "asc", "", ""));
        }
    }


}
