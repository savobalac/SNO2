package models;

import com.avaje.ebean.Page;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import utils.Utils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: Sav
 * Date: 16/10/13
 * Time: 12:59
 * Description: Model class that maps to DB table analyst.
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Users extends Model {

    // Instance variables
    @Id public Long                 id;

    @Constraints.Required
    public String                   username;

    @Constraints.Required
    public String                   password;

    @Constraints.Required
    public String                   email;

    public String                   fullname;
    public Timestamp                lastlogin;


    /**
     * Generic query helper for entity users with id Long
     */
    public static Finder<Long, Users> find = new Finder<Long, Users>(Long.class, Users.class);

    public static Users authenticate(String username, String password) {
        return find.where().eq("username", username)
                           .eq("password", password).findUnique();
    }

}
