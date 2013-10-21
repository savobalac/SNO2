package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;
import utils.Utils;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sav Balac
 * Date: 21/10/13
 * Time: 11:57
 * Description: Model class that maps to DB table note.
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Note extends Model {

    // Instance variables (Play! generates getters and setters)
    @Id public Long                 noteId;

    @Constraints.Required
    @ManyToOne public Users         user;

    @Constraints.Required
    public String                   title;

    @Constraints.Required
    @Lob @Column(name="content", length = Utils.MYSQL_TEXT_BYTES)
    public String                   content;

    @Constraints.Required
    @OneToOne public Analyst        analyst; // The analyst is the user making the note

    /**
     * Generic query helper for entity file with id Long
     */
    public static Finder<Long, Note> find = new Finder<Long, Note>(Long.class, Note.class);


}
