package controllers;

import com.avaje.ebean.Page;
import models.Analyst;
import models.Desk;
import models.DeskAnalyst;
import models.Users;
import play.api.data.validation.ValidationError;
import play.data.Form;
import static play.data.Form.*;

import play.mvc.*;
import views.html.Analysts.*;
import utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
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
            // Find the analyst record
            Analyst analyst = Analyst.find.byId(id);

            // Delete desks
            analyst.delAllDesks(); // Many-many

            // Delete associated files
            deleteDirectory("./public/uploads/analyst/" + id.toString() + "/profile");
            deleteDirectory("./public/uploads/analyst/" + id.toString() + "/document");
            deleteDirectory("./public/uploads/analyst/" + id.toString());

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
     * Display a (usually embedded) form to display and upload a profile image
     * @param id Id of the analyst
     * @return Result
     */
    public static Result editProfileImage(Long id) {
        Analyst analyst = Analyst.find.byId(id);
        return ok(tagAnalystImage.render(analyst));
    }


    /**
     * Display a (usually embedded) form to display and upload a CV document
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
     * Uploads a file to the file server and sets the analyst field value.
     * @param id Id of the analyst
     * @return Result
     */
    public static Result uploadFile(Long id, String fileType) {

        System.out.println("***** Analysts.uploadFile(), id: " + id + ", fileType: " + fileType + ". ***** ");

        String fileName = "";
        byte[] fileData = null;
        File file = null;
        FileOutputStream fos = null;
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
                    return ok("File " + fileName + " exceeds the maximum size allowed of " + Utils.MAX_FILE_SIZE_STRING + ".");
                }
                filePart = null;

                // Get the file data
                fileData = Files.readAllBytes(file.toPath());

                // Check if the analyst already has a file and delete it
                if (fileType.equals("profile")) {
                    if (analyst.profileImage != null) {
                        if (!analyst.profileImage.isEmpty()) {
                            deleteFile("./public/" + analyst.profileImage); // Static assets are in /public
                        }
                    }
                } else {
                    if (analyst.cvDocument != null) {
                        if (!analyst.cvDocument.isEmpty()) {
                            deleteFile("./public/" + analyst.cvDocument); // Static assets are in /public
                        }
                    }
                }

                // Create the directory if required
                File dir = new File("./public/uploads/analyst/" + id + "/" + fileType);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                // Save the new file to the file system
                File newFile = new File(dir, fileName);
                fos = new FileOutputStream(newFile);
                fos.write(fileData);
                fos.flush();
                fileData = null;

                // Set the analyst field and save
                String newPath = "uploads/analyst/" + id + "/" + fileType + "/" + fileName;
                if (fileType.equals("profile")) {
                    analyst.profileImage = newPath;
                } else {
                    analyst.cvDocument = newPath;
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
            try {
                if (fos!=null) {
                    fos.close();
                    fos = null;
                }
                if (file!=null) {
                    file = null;
                }
                System.gc();
            } catch (IOException ioe) {
                Utils.eHandler("Analysts.uploadFile()", ioe);
                ioe.printStackTrace();
            }
        }
    }


    /**
     * Deletes a file from the file system
     * @param path File path
     * @return boolean
     */
    private static boolean deleteFile(String path) {
        boolean isDeleted = false;
        File fileSys = new File(path);
        if (fileSys.exists()) {
            isDeleted = fileSys.delete();
        }
        fileSys = null;
        return isDeleted;
    }


    /**
     * Deletes a directory and all files in it from the file system
     * @param path File path
     * @return boolean
     */
    private static boolean deleteDirectory(String path) {

        // If the directory exists, delete files and then the directory
        boolean isDeleted = false;
        File dir = new File(path);
        if (dir.exists()) {
            // If the directory is empty, delete it
            if (dir.list().length==0) {
                isDeleted = dir.delete();
            } else {
                // Delete all files, then the directory
                String files[] = dir.list();
                for (String f : files) {
                    File fileDelete = new File(dir, f);
                    isDeleted = fileDelete.delete();
                }
                // The directory should be empty, so delete it
                if (dir.list().length==0) {
                    isDeleted = dir.delete();
                }
            }
        }
        return isDeleted; // Should be true if everything was deleted
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
