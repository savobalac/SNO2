package controllers;

import com.avaje.ebean.Page;
import models.Analyst;
import models.Desk;
import models.DeskAnalyst;
import models.Users;
import play.api.data.validation.ValidationError;
import play.data.Form;
import static play.data.Form.*;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import views.html.Analysts.*;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Sav
 * Date: 16/10/13
 * Time: 13:35
 * To change this template use File | Settings | File Templates.
 */
@Security.Authenticated(Secured.class) // All methods will require the user to be logged in
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
        // Get a page of analysts and render the list page
        Page<Analyst> pageAnalysts = Analyst.page(page, Application.RECORDS_PER_PAGE, sortBy, order, filter, search);
        Users user = Users.find.where().eq("username", request().username()).findUnique();
        return ok(listAnalysts.render(pageAnalysts, sortBy, order, filter, search, user));
    }


    /**
     * Create a new analyst.
     * @return Result
     */
    public static Result create() {
        return edit(new Long(0));
    }


    /**
     * Display a form to create a new or edit an existing analyst.
     * @param id    Id of the analyst to edit.
     * @return Result
     */
    public static Result edit(Long id) {
        Form<Analyst> analystForm;
        // New analysts have id = 0
        if (id <= 0L) {
            analystForm = Form.form(Analyst.class).fill(new Analyst());
        }
        else {
            Analyst analyst = Analyst.find.byId(id);
            System.out.println("edit() analyst.status.statusName = " + analyst.status.statusName); // New User
            analystForm = Form.form(Analyst.class).fill(Analyst.find.byId(id));
        }
        Users user = Users.find.where().eq("username", request().username()).findUnique();
        System.out.println("edit() statusId field value = " + analystForm.field("statusId").value()); // Null
        System.out.println("edit() rank field value = " + analystForm.field("rank").value());
        System.out.println("edit() primaryDesk field value = " + analystForm.field("primaryDesk").value());
        return ok(editAnalyst.render(((id<0)?(new Long(0)):(id)), analystForm, user));
    }


    /**
     * Update the analyst from the form.
     * @param id Id of the analyst to edit
     * @return Result
     */
    public static Result update(Long id) {
        // Get the analyst form and user
        Form<Analyst> analystForm = Form.form(Analyst.class).bindFromRequest();
        Users user = Users.find.where().eq("username", request().username()).findUnique();
        String msg;
        try {
            if (analystForm.hasErrors()) { // Return to the editAnalyst page
                return badRequest(editAnalyst.render(id, analystForm, user));
            } else {
                Analyst a = analystForm.get(); // Get the analyst data

                // Checkboxes if unchecked return null
                a.emailverified = (analystForm.field("emailverified").value() == null) ? (false) : (a.emailverified);
                a.phoneVerified = (analystForm.field("phoneVerified").value() == null) ? (false) : (a.phoneVerified);
                a.contractSigned = (analystForm.field("contractSigned").value() == null) ? (false) : (a.contractSigned);

                // Save if a new analyst, otherwise update and show a message
                a.saveOrUpdate();
                String fullName = analystForm.get().firstname + " " + analystForm.get().lastname;
                if (id==null || id==0) {
                    msg = "Analyst " + fullName + " has been created.";
                    flash(Utils.FLASH_KEY_SUCCESS, msg);
                } else {
                    msg = "Analyst " + fullName + " successfully updated.";
                    flash(Utils.FLASH_KEY_SUCCESS, msg);
                }
                // Redirect to remove analyst from query string
                return redirect(controllers.routes.Analysts.list(0, "lastname", "asc", "", ""));
            }
        } catch (Exception e) {
            // Log an error, show a message and return to the editAnalyst page
            Utils.eHandler("Analysts.update(" + id.toString() + ")", e);
            msg = String.format("Changes not saved. Error encountered ( %s ).", e.getMessage());
            flash(Utils.FLASH_KEY_ERROR, msg);
            return badRequest(editAnalyst.render(id, analystForm, user));
        }
    }


    /**
     * Delete the analyst.
     * @param id Id of the analyst to delete
     * @return Result
     */
    public static Result delete(Long id) {
        String msg;
        try {
            // Find the analyst record, delete it and show a message
            Analyst analyst = Analyst.find.byId(id);
            analyst.delete();
            msg = "Analyst deleted.";
            flash(Utils.FLASH_KEY_SUCCESS, msg);
        } catch (Exception e) {
            // Log an error and show a message
            Utils.eHandler("Analysts.delete(" + id.toString() + ")", e);
            msg = String.format("Error encountered (%s).", e.getMessage());
            flash(Utils.FLASH_KEY_ERROR, msg);
        } finally {
            // Redirect to remove analyst from query string
            return redirect(controllers.routes.Analysts.list(0, "lastname", "asc", "", ""));
        }
    }


    /**
     * Display a (usually embedded) form to display and upload a profile image
     * @param id Id of the analyst
     * @return Result
     */
    public static Result editProfileImage(Long id) {
        Analyst analyst = Analyst.find.byId(id);
        return ok(tagAnalystImage.render(analyst));
    }


    /**
     * Uploads the profile image.
     * @param id Id of the analyst
     * @return Result
     */
    public static Result uploadProfileImage(Long id) {
        return ok("OK"); // For now
        //return uploadFile(id, "imgFile");
    }


    /**
     * Uploads the CV document.
     * @param id Id of the analyst
     * @return Result
     */
    public static Result uploadCvDocument(Long id) {
        return ok("OK"); // For now
        //return uploadFile(id, "cvFile");
    }


    /**
     * Display a (usually embedded) form to display the desks an analyst is assigned to
     * @param id Id of the analyst
     * @return Result
     */
    public static Result editDesks(Long id) {
        Analyst analyst = Analyst.find.byId(id);
        return ok(tagListDeskAnalysts.render(analyst));
    }


    /**
     * Assigns the analyst to a desk.
     * @param id Id of the analyst
     * @param deskId Id of the desk
     * @return Result
     */
    public static Result addDesk(Long id, Long deskId) {
        Analyst analyst = Analyst.find.byId(id);
        Desk desk = Desk.find.byId(deskId);
        try {
            if(analyst == null) {
                return ok("Error: Analyst not found.");
            }
            else {
                analyst.addDesk(desk);
                return ok("OK"); // "OK" is used by the calling Ajax function
            }
        }
        catch (Exception e) {
            Utils.eHandler("Analysts.addDesk()", e);
            return ok(e.getMessage());
        }
    }


    /**
     * Deletes a desk from the analyst.
     * @param id Id of the analyst
     * @param deskId Id of the desk
     * @return Result
     */
    public static Result delDesk(Long id, Long deskId) {
        Analyst analyst = Analyst.find.byId(id);
        Desk desk = Desk.find.byId(deskId);
        try {
            if (analyst == null) {
                return ok("Error: Analyst not found.");
            }
            if (desk == null) {
                return ok("Error: Desk not found.");
            } else {
                analyst.delDesk(desk);
                return ok("OK"); // "OK" is used by the calling Ajax function
            }
        }
        catch (Exception e) {
            Utils.eHandler("Analysts.delDesk()", e);
            return ok(e.getMessage());
        }
    }


}
