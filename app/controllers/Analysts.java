package controllers;

import com.avaje.ebean.Page;
import models.Analyst;
import models.Desk;
import models.User;
import models.S3File;
import play.data.Form;

import play.mvc.*;
import views.html.Analysts.*;
import utils.Utils;

import java.io.File;
import java.nio.file.Files;

/**
 * Analysts controller with methods to list, create, edit, update and delete.
 * There are also methods to render and modify an analyst's profile, CV and desks.
 *
 * Date:        16/10/13
 * Time:        13:35
 *
 * @author      Sav Balac
 * @version     %I%, %G%
 * @since       1.0
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
        User user = User.find.where().eq("username", request().username()).findUnique();
        return ok(listAnalysts.render(pageAnalysts, sortBy, order, filter, search, user));
    }


    /**
     * Creates a new analyst.
     * @return Result
     */
    public static Result create() {
        return edit(new Long(0));
    }


    /**
     * Displays a form to create a new or edit an existing analyst.
     * @param id Id of the analyst to edit
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
            analystForm = Form.form(Analyst.class).fill(Analyst.find.byId(id));
        }
        User user = User.find.where().eq("username", request().username()).findUnique();
        return ok(editAnalyst.render(((id<0)?(new Long(0)):(id)), analystForm, user));
    }


    /**
     * Updates the analyst from the form.
     * @param id Id of the analyst to update
     * @return Result
     */
    public static Result update(Long id) {
        // Get the analyst form and user
        Form<Analyst> analystForm = Form.form(Analyst.class).bindFromRequest();
        User user = User.find.where().eq("username", request().username()).findUnique();
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

                // Save if a new analyst, otherwise update, and show a message
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
     * Deletes the analyst.
     * @param id Id of the analyst to delete
     * @return Result
     */
    public static Result delete(Long id) {
        String msg;
        try {
            // Find the analyst record
            Analyst analyst = Analyst.find.byId(id);

            // Delete desks
            analyst.delAllDesks(); // Many-many

            // Delete the analyst
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
     * Displays a (usually embedded) form to display and upload a profile image.
     * @param id Id of the analyst
     * @return Result
     */
    public static Result editProfileImage(Long id) {
        Analyst analyst = Analyst.find.byId(id);
        return ok(tagAnalystImage.render(analyst));
    }


    /**
     * Displays a (usually embedded) form to display and upload a CV document.
     * @param id Id of the analyst
     * @return Result
     */
    public static Result editCvDocument(Long id) {
        Analyst analyst = Analyst.find.byId(id);
        return ok(tagAnalystCV.render(analyst));
    }


    /**
     * Uploads the profile image.
     * @param id Id of the analyst
     * @return Result
     */
    public static Result uploadProfileImage(Long id) {
        return uploadFile(id, "profile");
    }


    /**
     * Uploads the CV document.
     * @param id Id of the analyst
     * @return Result
     */
    public static Result uploadCvDocument(Long id) {
        return uploadFile(id, "document");
    }

    
    /**
     * Uploads a file to AWS S3 and sets the analyst field value.
     * @param id Id of the analyst
     * @return Result
     */
    public static Result uploadFile(Long id, String fileType) {
        String fileName = "";
        File file = null;
        String msg = "";

        // Get the analyst record
        Analyst analyst = Analyst.find.byId(id);
        try {
            // This should be prevented by the view, but test anyway
            if (id == 0) {
                msg = "Analyst must be saved before uploading files.";
                return ok(msg);
            }
            if (analyst == null) {
                msg = "Analyst not found.";
                return ok(msg);
            }

            // Get the file part from the form
            Http.MultipartFormData body = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart filePart = body.getFile(fileType);

            // Check the file exists
            if (filePart != null) {
                fileName = filePart.getFilename();

                // If uploading a profile image, check it is an image
                if (fileType.equals("profile")) {
                    String contentType = filePart.getContentType();
                    if (!contentType.startsWith("image/")) {
                        msg = "File " + fileName + " is not an image. File not saved.";
                        return ok(msg);
                    }
                }

                // Check if the file exceeds the maximum allowable size
                file = filePart.getFile();
                if (Files.size(file.toPath()) > Utils.MAX_FILE_SIZE) {
                    return ok("File " + fileName +
                              " exceeds the maximum size allowed of " + Utils.MAX_FILE_SIZE_STRING + ".");
                }

                // Check if the analyst already has a file and delete it
                if (fileType.equals("profile")) {
                    if (analyst.profileImage != null) {
                        S3File profileImage = S3File.find.byId(analyst.profileImage.id);
                        profileImage.delete();
                    }
                } else {
                    if (analyst.cvDocument != null) {
                        S3File cvDocument = S3File.find.byId(analyst.cvDocument.id);
                        cvDocument.delete();
                    }
                }

                // Save the new file to AWS S3
                S3File s3File = new S3File();
                s3File.name = filePart.getFilename();
                s3File.file = filePart.getFile();
                s3File.save();
                filePart = null;

                // Save the s3File to the analyst
                if (fileType.equals("profile")) {
                    analyst.profileImage = s3File;
                } else {
                    analyst.cvDocument = s3File;
                }
                analyst.saveOrUpdate();

                msg = "File " + fileName + " successfully uploaded to analyst " + analyst.getFullName();
                flash(Utils.FLASH_KEY_SUCCESS, msg);
                msg = "OK"; // The AJAX call from editAnalyst tests for "OK"
                return ok(msg);
            } else { // File not found
                msg = "Please select a file.";
                return ok(msg);
            }
        } catch (Exception e) {
            Utils.eHandler("Analysts.uploadFile()", e);
            msg = String.format("Error encountered, changes not saved (%s).", e.getMessage());
            return ok(msg);
        } finally {
            // Free up resources
            if (file != null) {
                file = null;
            }
            System.gc();
        }
    }


    /**
     * Displays a (usually embedded) form to display the desks an analyst is assigned to.
     * @param id Id of the analyst
     * @return Result
     */
    public static Result editDesks(Long id) {
        Analyst analyst = Analyst.find.byId(id);
        return ok(tagListDeskAnalysts.render(analyst));
    }


    /**
     * Assigns the analyst to a desk.
     * @param id      Id of the analyst
     * @param deskId  Id of the desk
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
     * @param id      Id of the analyst
     * @param deskId  Id of the desk
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
