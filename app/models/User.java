package models;

import com.avaje.ebean.Page;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import utils.Utils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Model class that maps to DB table user.
 * Contains a method to authenticate the user.
 *
 * Date: 16/10/13
 * Time: 12:59
 *
 * @author      Sav Balac
 * @version     %I%, %G%
 * @since       1.0
 */
@Entity
public class User extends Model {

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

    @ManyToMany(mappedBy="users")
    public List<Group>              groups;


    /**
     * Generic query helper for entity User.
     */
    public static Finder<Long, User> find = new Finder<Long, User>(Long.class, User.class);


    /**
     * Returns a page of users.
     *
     * @param page          Page to display
     * @param pageSize      Number of users per page
     * @param sortBy        User property used for sorting
     * @param order         Sort order (either or asc or desc)
     * @param filter        Filter applied on group name
     * @param search        Search applied on fullname
     * @return Page<User>
     */
    public static Page<User> page(int page, int pageSize, String sortBy, String order, String filter, String search) {

        // Search on fullname, otherwise filter on group name if it's set
        Page p = null;
        if (search.isEmpty()) {
            //if (filter.isEmpty()) { // Get all records
                p = find.where()
                        .orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .getPage(page);
            //} else { // Filter
            //    p = find.where()
            //            .ilike("primaryDesk.name", "%" + filter + "%")
            //            .orderBy(sortBy + " " + order)
            //            .findPagingList(pageSize)
            //            .getPage(page);
            //}
        } else { // Search
            p = find.where()
                    .ilike("fullname", "%" + search + "%")
                    .orderBy(sortBy + " " + order)
                    .findPagingList(pageSize)
                    .getPage(page);
        }
        return p;
    }


    /**
     * Saves or updates the user.
     * @throws Exception If there was a problem updating the DB
     */
    public void saveOrUpdate() throws Exception {
        // The id should be 0 for a new record
        if (id==null || id<=0) {
            save();
        } else {
            update();
        }
    }


    /**
     * Authenticates the user. The hashed password is 64 characters long.
     * @param username Username
     * @param password Password to be hashed before checking
     * @return Result
     * @throws NoSuchAlgorithmException    If the hashing algorithm doesn't exist
     */
    public static User authenticate(String username, String password) throws NoSuchAlgorithmException {
        return find.where().eq("username", username)
                           .eq("password", Utils.hashString(password)).findUnique();
    }


}
