package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;
import utils.Utils;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: Sav Balac
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


    /**
     * Authenticates the user. The hashed password is 64 characters long.
     * @param username Username
     * @param password Password to be hashed before checking
     * @return Result
     */
    public static Users authenticate(String username, String password) throws NoSuchAlgorithmException {
        return find.where().eq("username", username)
                           .eq("password", Utils.hashString(password)).findUnique();
    }


}
