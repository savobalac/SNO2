package controllers;

import com.avaje.ebean.Page;
import models.Analyst;
import models.Users;
import play.data.Form;
import static play.data.Form.*;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import views.html.Analysts.*;
import utils.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: Sav
 * Date: 16/10/13
 * Time: 13:35
 * To change this template use File | Settings | File Templates.
 */
@Security.Authenticated(Secured.class)
public class Analysts extends Controller {

    /**
     * Displays a paginated list of analysts.
     *
     * @param page          Current page number (starts from 0)
     * @param sortBy        Column to be sorted
     * @param order         Sort order (either asc or desc)
     * @param filter        Filter applied on <column name>
     * @param search        Filter applied on <column names>
     * @return Result
     */
    public static Result list(int page, String sortBy, String order, String filter, String search) {
        Page<Analyst> pageSongs = Analyst.page(page, Application.RECORDS_PER_PAGE, sortBy, order, filter, search);
        //return ok("Test list analysts page");
        Users user = Users.find.where().eq("username", request().username()).findUnique();
        return ok(listAnalysts.render(pageSongs, sortBy, order, filter, search, user));
    }

    /**
     * Create a new Song.
     * @return Result
     */
    public static Result create() {
        return edit(new Long(0));
        //return ok("No create function yet");
    }

    /**
     * Display a form to create a new or edit an existing analyst.
     * @param id    Id of the analyst to edit. 0 == Create New.
     * @return Result
     */
    public static Result edit(Long id) {
        Form<Analyst> analystForm;
        if (id <= 0L) {
            //analystForm.fill(new Analyst());
            analystForm = Form.form(Analyst.class).fill(new Analyst());
        }
        else {
            //analystForm.fill(Analyst.find.byId(id));
            analystForm = Form.form(Analyst.class).fill(Analyst.find.byId(id));
        }
        Users user = Users.find.where().eq("username", request().username()).findUnique();
        return ok(editAnalyst.render(((id<0)?(new Long(0)):(id)), analystForm, user));
    }

    /**
     * Update the analyst from the form.
     * @param id Id of the analyst to edit
     * @return Result
     */
    public static Result update(Long id) {
        /*Form<Analyst> analystForm = Form.form(Analyst.class).bindFromRequest();
        Users user = Users.find.where().eq("username", request().username()).findUnique();
        String msg;
        try {
            if(analystForm.hasErrors()) {
                return badRequest(editAnalyst.render(id, analystForm, user));
            } else {
                Analyst a = analystForm.get();
                // Checkboxes if unchecked return null
                a.emailverified = (analystForm.field("emailverified").value() == null) ? (false) : (a.emailverified);
                a.phoneVerified = (analystForm.field("phoneVerified").value() == null) ? (false) : (a.phoneVerified);
                a.contractSigned = (analystForm.field("contractSigned").value() == null) ? (false) : (a.contractSigned);
                return ok("No update function yet");
            }
        } catch (Exception e) {
            Utils.eHandler("Admin.updateFeature(" + id.toString() + ")", e);
            msg = String.format("Changes NOT SAVED. Error encountered ( %s ).", e.getMessage());
            flash(Utils.FLASH_KEY_ERROR, msg);
            return badRequest(editAnalyst.render(id, analystForm, user));
        }*/
        return ok("No update function yet");
    }


    /**
     * Delete the analyst.
     * @param id Id of the analyst to delete
     * @return Result
     */
    public static Result delete(Long id) {
        return ok("No delete function yet");
    }

}
