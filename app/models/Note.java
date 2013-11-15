package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;
import utils.Utils;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Model class that maps to DB table note.
 *
 * Date: 21/10/13
 * Time: 11:57
 *
 * @author      Sav Balac
 * @version     %I%, %G%
 * @since       1.0
 */
@Entity
public class Note extends Model {

    // Instance variables (Play! generates getters and setters)
    @Id public Long                 noteId;

    @ManyToOne public User          user;

    @Constraints.Required
    @Formats.NonEmpty
    public String                   title;

    @Constraints.Required
    @Formats.NonEmpty
    @Lob @Column(name="content", length = Utils.MYSQL_TEXT_BYTES)
    public String                   content;

    @ManyToOne @JoinColumn(name="analyst_id")
    public Analyst                  analyst; // Many notes may be written about an analyst

    public Timestamp                createdDt;

    @ManyToOne @JoinColumn(name="updated_by")
    public User                     updatedBy;

    public Timestamp                updatedDt;


    /**
     * Generic query helper for entity Note.
     */
    public static Finder<Long, Note> find = new Finder<Long, Note>(Long.class, Note.class);


    /**
     * Saves or updates the note.
     * @throws Exception If there was a problem updating the DB
     */
    public void saveOrUpdate() throws Exception {
        // The note id should be 0 for a new record
        if (noteId==null || noteId<=0) {
            save();
        } else {
            update();
        }
    }


}
