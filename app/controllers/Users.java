package controllers;

import com.avaje.ebean.Page;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
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
        // Get a page of analysts and render the list page
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
        //return ok(editUser.render(((id<0)?(new Long(0)):(id)), userForm, user));
        return ok("editUser page not yet written");
    }


}
