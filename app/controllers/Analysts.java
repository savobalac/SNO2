package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.avaje.ebean.Page;
import models.Analyst;
import models.Desk;
import models.Note;
import models.User;
import models.S3File;
import play.data.Form;

import play.mvc.*;
import views.html.Analysts.*;
import utils.Utils;

import java.io.File;
import java.nio.file.Files;
import org.joda.time.DateTime;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import static play.libs.Json.toJson;

/**
 * Analysts controller with methods to list, create, edit, update and delete.
 * There are methods to list and modify desks, list and modify notes, and show and upload a profile and CV.
 *
 * Date:        16/10/13
 * Time:        13:35
 *
 * @author      Sav Balac
 * @version     1.2
 */
@Security.Authenticated(Secured.class) // All methods will require the user to be logged in
public class Analysts extends AbstractController {


    /**
     * Displays a paginated list of analysts.
     *
     * @param page          Current page number (starts from 0).
     * @param sortBy        Column to be sorted.
     * @param order         Sort order (either asc or desc).
     * @param filter        Filter applied on primary desk name.
     * @param search        Search applied on last name.
     * @return Result  The list page or all analysts as JSON.
     */
    public static Result list(int page, String sortBy, String order, String filter, String search) {

        // Return data in HTML or JSON as requested
        if (request().accepts("text/html")) {
            // Get a page of analysts and render the list page
            Page<Analyst> pageAnalysts = Analyst.page(page, Application.RECORDS_PER_PAGE, sortBy, order, filter, search);
            return ok(listAnalysts.render(pageAnalysts, sortBy, order, filter, search, getLoggedInUser()));
        } else if (request().accepts("application/json") || request().accepts("text/json")) {
            return ok(Analyst.getAllAsJson(getLoggedInUser()));
        } else {
            return badRequest();
        }
    }


    /**
     * Creates a new analyst.
     *
     * @return Result  The edit page.
     */
    public static Result create() {
        return edit(0L);
    }


    /**
     * Displays a form to create a new or edit an existing analyst.
     *
     * @param id  Id of the analyst to edit.
     * @return Result  The edit page or the analyst as JSON.
     */
    public static Result edit(Long id) {

        // New analysts have id 0 and don't exist
        Form<Analyst> analystForm;
        Analyst analyst;
        if (id <= 0L) {
            analyst = new Analyst();
        } else {
            // Check analyst exists and return if not
            analyst = Analyst.find.byId(id);
            if (analyst == null) {
                return noAnalyst(id);
            }
        }
        analystForm = Form.form(Analyst.class).fill(analyst);

        // Return data in HTML or JSON as requested
        if (request().accepts("text/html")) {
            return ok(editAnalyst.render(((id<0)?(0L):(id)), analystForm, getLoggedInUser()));
        } else if (request().accepts("application/json") || request().accepts("text/json")) {
            return ok(analyst.toJson(getLoggedInUser()));
        } else {
            return badRequest();
        }
    }


    /**
     * Returns either the list page or a JSON message when the analyst doesn't exist.
     *
     * @param  id  Id of the analyst.
     * @return Result  The list page or a JSON message.
     */
    private static Result noAnalyst(Long id) {
        // Return data in HTML or JSON as requested
        if (request().accepts("text/html")) {
            return redirect(controllers.routes.Analysts.list(0, "lastname", "asc", "", ""));
        } else if (request().accepts("application/json") || request().accepts("text/json")) {
            return ok(getErrorAsJson("Analyst: " + id + " does not exist."));
        } else {
            return badRequest();
        }
    }


    /**
     * Updates the analyst from the form.
     *
     * @param id  Id of the analyst to update.
     * @return Result  The list page or the edit page if in error.
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result update(Long id) {
        User loggedInUser = getLoggedInUser();
        Form<Analyst> analystForm = null;
        try {
            analystForm = Form.form(Analyst.class).bindFromRequest(); // Get the form data
            // Check if there are errors
            if (analystForm.hasErrors()) {
                // Return data in HTML or JSON as requested
                if (request().accepts("text/html")) {
                    return badRequest(editAnalyst.render(id, analystForm, loggedInUser)); // Return to the editAnalyst page
                } else if (request().accepts("application/json") || request().accepts("text/json")) {
                    return ok(getErrorsAsJson(analystForm));
                } else {
                    return badRequest();
                }
            } else {
                Analyst newAnalyst = analystForm.get(); // Get the analyst data

                // Updates have a non-zero id
                if (id != 0) {
                    // Check analyst exists and return if not
                    Analyst existingAnalyst = Analyst.find.byId(id);
                    if (existingAnalyst == null) {
                        return noAnalyst(id);
                    }

                    // Check id supplied by the form is the same as the id parameter (only possible via JSON)
                    if (!newAnalyst.analystId.equals(id)) {
                        return ok(getErrorAsJson("Analyst id in the data (" + newAnalyst.analystId + ") " +
                                                 "does not match the analyst id in the URL (" + id + ")."));
                    }

                    // Check if the status has changed and, if so, do some processing (yet to be defined)
                    if (!newAnalyst.status.equals(existingAnalyst.status)) {
                        System.out.println("***** Status id changed from: " + existingAnalyst.status.statusId +
                                                                    " to: " + newAnalyst.status.statusId);
                    }

                    // If updating from HTML, disabled checkboxes have an associated disabled field with the original value
                    // Checkboxes, if unchecked or disabled, return null
                    if (request().accepts("text/html")) {
                        if (analystForm.field("disEmailverified").value() != null) {
                            newAnalyst.emailverified = (analystForm.field("disEmailverified").value().equals("true"));
                        } else {
                            newAnalyst.emailverified =
                                (analystForm.field("emailverified").value() == null) ? (false) : (newAnalyst.emailverified);
                        }
                        if (analystForm.field("disPhoneverified").value() != null) {
                            newAnalyst.phoneVerified = (analystForm.field("disPhoneverified").value().equals("true"));
                        } else {
                            newAnalyst.phoneVerified =
                                (analystForm.field("phoneVerified").value() == null) ? (false) : (newAnalyst.phoneVerified);
                        }
                        if (analystForm.field("disContractSigned").value() != null) {
                            newAnalyst.contractSigned = (analystForm.field("disContractSigned").value().equals("true"));
                        } else {
                            newAnalyst.contractSigned =
                                (analystForm.field("contractSigned").value() == null) ? (false) : (newAnalyst.contractSigned);
                        }

                    // Updating from JSON. The following fields are invisible or disabled, so ignore the form values
                    } else {

                        // If the logged-in user is not an admin user or manager,
                        //     phone and emailAlternate are invisible.
                        if (!loggedInUser.isAdminOrManager()) {
                            newAnalyst.phone            = existingAnalyst.phone;
                            newAnalyst.emailAlternate   = existingAnalyst.emailAlternate;

                            // If the logged-in user is not an admin user, manager or staff,
                            //     paypalAccountEmail and all address fields are invisible;
                            //     rank, emailverified, phoneVerified, contractSigned and wikiUsername are disabled.
                            if (!loggedInUser.isStaff()) {
                                newAnalyst.paypalAccountEmail   = existingAnalyst.paypalAccountEmail;
                                newAnalyst.address1             = existingAnalyst.address1;
                                newAnalyst.address2             = existingAnalyst.address2;
                                newAnalyst.city                 = existingAnalyst.city;
                                newAnalyst.state                = existingAnalyst.state;
                                newAnalyst.zip                  = existingAnalyst.zip;
                                newAnalyst.country              = existingAnalyst.country;
                                newAnalyst.countryOfResidence   = existingAnalyst.countryOfResidence;
                                newAnalyst.rank                 = existingAnalyst.rank;
                                newAnalyst.emailverified        = existingAnalyst.emailverified;
                                newAnalyst.phoneVerified        = existingAnalyst.phoneVerified;
                                newAnalyst.contractSigned       = existingAnalyst.contractSigned;
                                newAnalyst.wikiUsername         = existingAnalyst.wikiUsername;
                            }
                        }
                    }
                }

                // Save if a new analyst, otherwise update, and show a message
                newAnalyst.saveOrUpdate();
                String fullName = analystForm.get().firstname + " " + analystForm.get().lastname;
                String msg;
                if (id == 0) {
                    msg = "Analyst: " + fullName + " created.";
                } else {
                    msg = "Analyst: " + fullName + " updated.";
                }

                // Return data in HTML or JSON as requested
                if (request().accepts("text/html")) {
                    // Redirect to the list page and remove the analyst from the query string
                    flash(Utils.KEY_SUCCESS, msg);
                    return redirect(controllers.routes.Analysts.list(0, "lastname", "asc", "", ""));
                } else if (request().accepts("application/json") || request().accepts("text/json")) {
                    return ok(getSuccessAsJson(msg));
                } else {
                    return badRequest();
                }
            }
        } catch (Exception e) {
            // Log an error
            Utils.eHandler("Analysts.update(" + id + ")", e);

            // Return data in HTML or JSON as requested
            if (request().accepts("text/html")) {
                // Show a message and return to the editAnalyst page
                showSaveError(e); // Method in AbstractController
                return badRequest(editAnalyst.render(id, analystForm, loggedInUser));
            } else if (request().accepts("application/json") || request().accepts("text/json")) {
                String msg;
                if (id == 0) {
                    msg = "Analyst not created.";
                } else {
                    msg = "Analyst: " + id + " not updated.";
                }
                msg += " Error: " + e.getMessage();
                return ok(getErrorAsJson(msg));
            } else {
                return badRequest();
            }
        }
    }


    /**
     * Deletes the analyst.
     * @param id Id of the analyst to delete
     * @return Result
     */
    public static Result delete(Long id) {
        try {
            // Check analyst exists and return if not
            Analyst analyst = Analyst.find.byId(id);
            if (analyst == null) {
                return noAnalyst(id);
            }
            String fullName = analyst.getFullName();

            // Delete desks and notes
            analyst.delAllDesks();
            analyst.delAllNotes();

            // Delete profile image and CV (no foreign keys)
            if (analyst.profileImage != null) {
                S3File.find.byId(analyst.profileImage.id).delete();
            }
            if (analyst.cvDocument != null) {
                S3File.find.byId(analyst.cvDocument.id).delete();
            }

            // Delete the analyst
            analyst.delete();
            String msg = "Analyst: " + fullName + " deleted.";

            // Return data in HTML or JSON as requested
            if (request().accepts("text/html")) {
                flash(Utils.KEY_SUCCESS, msg);
                return redirect(controllers.routes.Analysts.list(0, "lastname", "asc", "", ""));
            } else if (request().accepts("application/json") || request().accepts("text/json")) {
                return ok(getSuccessAsJson(msg));
            } else {
                return badRequest();
            }
        } catch (Exception e) {
            // Log an error
            Utils.eHandler("Analysts.delete(" + id + ")", e);
            String msg = "Analyst: " + id + " not deleted. Error: " + e.getMessage();
            // Return data in HTML or JSON as requested
            if (request().accepts("text/html")) {
                showSaveError(e);
                return redirect(controllers.routes.Analysts.list(0, "lastname", "asc", "", ""));
            } else if (request().accepts("application/json") || request().accepts("text/json")) {
                return ok(getErrorAsJson(msg));
            } else {
                return badRequest();
            }
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
        File file = null;
        try {
            if (id == 0) { // Uploads to new analysts should be prevented by the view, but test anyway
                return ok("Analyst must be saved before uploading files.");
            }
            // Get the analyst
            Analyst analyst = Analyst.find.byId(id);
            if (analyst == null) {
                return ok("ERROR: Analyst not found. File not saved.");
            }

            // Get the file part from the form
            Http.MultipartFormData body = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart filePart = body.getFile(fileType);

            // Check the file exists
            String fileName = "";
            if (filePart != null) {
                fileName = filePart.getFilename();

                // If uploading a profile image, check it is an image
                if (fileType.equals("profile")) {
                    String contentType = filePart.getContentType();
                    if (!contentType.startsWith("image/")) {
                        return ok("File " + fileName + " is not an image. File not saved.");
                    }
                }

                // Check if the file exceeds the maximum allowable size
                file = filePart.getFile();
                if (Files.size(file.toPath()) > Utils.MAX_FILE_SIZE) {
                    return ok("File " + fileName + " exceeds the maximum size allowed of " +
                              Utils.MAX_FILE_SIZE_STRING + ". File not saved.");
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

                // Save the s3File to the analyst, update and show a message
                if (fileType.equals("profile")) {
                    analyst.profileImage = s3File;
                } else {
                    analyst.cvDocument = s3File;
                }
                analyst.update();
                flash(Utils.KEY_SUCCESS,
                      "File: " + fileName + " uploaded to analyst: " + analyst.getFullName());
                return ok("OK"); // The Ajax call from editAnalyst checks for "OK"
            } else { // File not found
                return ok("Please select a file.");
            }
        } catch (Exception e) {
            Utils.eHandler("Analysts.uploadFile(" + id + ", " + fileType + ")", e);
            return ok(String.format("%s Changes not saved.", e.getMessage()));
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
        return changeDesk(id, deskId, "add");
    }


    /**
     * Deletes a desk from the analyst.
     * @param id      Id of the analyst
     * @param deskId  Id of the desk
     * @return Result
     */
    public static Result delDesk(Long id, Long deskId) {
        return changeDesk(id, deskId, "delete");
    }


    /**
     * Adds or deletes a desk to/from the analyst.
     * @param id      Id of the analyst
     * @param deskId  Id of the desk
     * @param action  "add" or "delete"
     * @return Result
     */
    private static Result changeDesk(Long id, Long deskId, String action) {
        Analyst analyst = Analyst.find.byId(id);
        Desk desk = Desk.find.byId(deskId);
        try {
            if (analyst == null) {
                return ok("ERROR: Analyst not found. Changes not saved.");
            }
            if (desk == null) {
                return ok("ERROR: Desk not found. Changes not saved.");
            } else {
                // Add, delete or return an error
                if (action.equals("add")) {
                    analyst.addDesk(desk);
                } else if (action.equals("delete")) {
                    analyst.delDesk(desk);
                } else {
                    return ok("ERROR: incorrect action, use add or delete");
                }
                return ok("OK"); // "OK" is used by the calling Ajax function
            }
        }
        catch (Exception e) {
            Utils.eHandler("Analysts.changeDesk(" + id + ", " + deskId + ", " + action + ")", e);
            return ok("ERROR: " + e.getMessage());
        }
    }


    /**
     * Displays a (usually embedded) form to display the notes that have been written about an analyst.
     * @param id Id of the analyst
     * @return Result
     */
    public static Result editNotes(Long id) {
        Analyst analyst = Analyst.find.byId(id);
        return ok(tagListNotes.render(analyst));
    }


    /**
     * Creates a new note.
     * @return Result
     */
    public static Result createNote(Long aId) {
        return editNote(aId, 0L);
    }


    /**
     * Displays a form to create a new or edit an existing note.
     * @param aId Id of the analyst
     * @param nId Id of the note to edit
     * @return Result
     */
    public static Result editNote(Long aId, Long nId) {

        // Get the analyst
        Analyst analyst = Analyst.find.byId(aId);

        // New notes have id 0
        Form<Note> noteForm;
        if (nId <= 0L) {
            Note note = new Note();
            note.analyst = analyst; // Set the analyst
            noteForm = Form.form(Note.class).fill(note);
        }
        else {
            noteForm = Form.form(Note.class).fill(Note.find.byId(nId));
        }
        return ok(editNote.render(((nId<0)?(0L):(nId)), aId, noteForm, getLoggedInUser()));

    }


    /**
     * Updates the note from the form.
     * @param aId Id of the analyst
     * @param nId Id of the note to edit
     * @return Result
     */
    public static Result updateNote(Long aId, Long nId) {
        Form<Note> noteForm = Form.form(Note.class).bindFromRequest(); // Get the form data
        User loggedInUser = getLoggedInUser();
        try {
            if (noteForm.hasErrors()) { // Return to the editNote page
                return badRequest(editNote.render(nId, aId, noteForm, loggedInUser));
            } else {
                // Get the analyst and note data
                Analyst analyst = Analyst.find.byId(aId);
                Note note = noteForm.get();

                // Get the current date and time
                DateTime now = Utils.getCurrentDateTime();

                // Save if a new note, otherwise update, add the note to the analyst and show a message
                String msg;
                if (nId == 0) {
                    note.analyst = analyst;
                    note.user = loggedInUser;
                    note.createdDt = now;
                    note.save();
                    analyst.addNote(note);
                    msg = "Note: " + note.title + " created.";
                } else { // Get the currently-stored note
                    Note existingNote = Note.find.byId(nId);
                    note.createdDt = existingNote.createdDt; // Ensure the created datetime isn't overwritten
                    note.updatedBy = loggedInUser;
                    note.updatedDt = now;
                    note.update();
                    msg = "Note: " + note.title + " updated.";
                }
                flash(Utils.KEY_SUCCESS, msg);

                // Redirect to the edit analyst page
                return redirect(controllers.routes.Analysts.edit(aId));
            }
        } catch (Exception e) {
            // Log an error, show a message and return to the editNote page
            Utils.eHandler("Analysts.updateNote(" + aId + ", " + nId + ")", e);
            showSaveError(e); // Method in AbstractController
            return badRequest(editNote.render(nId, aId, noteForm, loggedInUser));
        }
    }


    /**
     * Deletes a note from the analyst. This version is called from the note list via Ajax.
     * @param aId     Id of the analyst
     * @param noteId  Id of the note
     * @return Result
     */
    public static Result delNote(Long aId, Long noteId) {
        Analyst analyst = Analyst.find.byId(aId);
        Note note = Note.find.byId(noteId);
        try {
            if (analyst == null) {
                return ok("ERROR: Analyst not found. Changes not saved.");
            }
            if (note == null) {
                return ok("ERROR: Note not found. Changes not saved.");
            } else {
                // Remove the note from the analyst and delete it
                analyst.delNote(note);
                note.delete();
                return ok("OK"); // "OK" is used by the calling Ajax function
            }
        }
        catch (Exception e) {
            Utils.eHandler("Analysts.delNote(" + aId + ", " + noteId + ")", e);
            return ok("ERROR: " + e.getMessage());
        }
    }


    /**
     * Deletes the analyst. This version is called from the editNote page.
     * @param aId     Id of the analyst
     * @param noteId  Id of the note
     * @return Result
     */
    public static Result deleteNote(Long aId, Long noteId) {
        try {
            // Find the analyst and note
            Analyst analyst = Analyst.find.byId(aId);
            Note note = Note.find.byId(noteId);
            String title = note.title;

            // Remove the note from the analyst, delete it and show a message
            analyst.delNote(note);
            note.delete();
            flash(Utils.KEY_SUCCESS, "Note: " + title + " deleted.");
        } catch (Exception e) {
            // Log an error and show a message
            Utils.eHandler("Analysts.deleteNote(" + aId + ", " + noteId + ")", e);
            showSaveError(e); // Method in AbstractController
        } finally {
            // Redirect to the edit analyst page
            return redirect(controllers.routes.Analysts.edit(aId));
        }
    }


}
