package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;
import utils.Utils;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Sav Balac
 * Date: 21/10/13
 * Time: 11:57
 * Description: Model class that maps to DB table file.
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class File extends Model {

    // Instance variables (Play! generates getters and setters)
    @Id public Long                 fileId;

    @Constraints.Required
    @ManyToOne public Analyst       analyst;

    @Lob @Column(name="link", length = Utils.MYSQL_TEXT_BYTES)
    public String                   link;

    @Lob @Column(name="description", length = Utils.MYSQL_TEXT_BYTES)
    public String                   description;


    /**
     * Generic query helper for entity file with id Long
     */
    public static Finder<Long, File> find = new Finder<Long, File>(Long.class, File.class);


}
