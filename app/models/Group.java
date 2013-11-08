package models;

import com.avaje.ebean.Page;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Model class that maps to DB table group.
 * Contains methods to get groups.
 *
 * Date: 08/11/13
 * Time: 12:52
 *
 * @author      Sav Balac
 * @version     %I%, %G%
 * @since       1.0
 */
@Entity
@Table(name="groups") // Can't have a database table called "group"
public class Group extends Model {

    // Instance variables (Play! generates getters and setters)
    @Id public Long                 id;

    @Constraints.Required
    public String                   name;

    @ManyToMany @JoinTable(name="usergroup",
        joinColumns={@JoinColumn(name="group_id", referencedColumnName="id")},
        inverseJoinColumns={@JoinColumn(name="user_id", referencedColumnName="id")}
    ) public List<User>             users; // The columns are in usergroup, the referenced columns are in group and user


    /**
     * Generic query helper for entity Group.
     */
    public static Finder<Long, Group> find = new Finder<Long, Group>(Long.class, Group.class);


    /**
     * Returns a page of groups.
     *
     * @param page          Page to display
     * @param pageSize      Number of groups per page
     * @param sortBy        User property used for sorting
     * @param order         Sort order (either or asc or desc)
     * @param filter        Filter applied on column name
     * @param search        Search applied on name
     * @return Page<Group>
     */
    public static Page<Group> page(int page, int pageSize, String sortBy, String order, String filter, String search) {

        // Search on name
        Page p = null;
        if (search.isEmpty()) { // Get all records
            p = find.where()
                    .orderBy(sortBy + " " + order)
                    .findPagingList(pageSize)
                    .getPage(page);
        } else { // Search
            p = find.where()
                    .ilike("name", "%" + search + "%")
                    .orderBy(sortBy + " " + order)
                    .findPagingList(pageSize)
                    .getPage(page);
        }
        return p;
    }


    /**
     * Saves or updates the group.
     * @throws Exception If there was a problem updating the DB
     */
    public void saveOrUpdate() throws Exception {
        // The id should be 0 for a new record
        if (id==null || id<=0) {
            // Check for duplicate Group name
            Group group = Group.find.where().eq("name", this.name).findUnique();
            if (group != null) {
                throw new Exception("Group with same name already exists");
            }
            save();
        } else {
            update();
        }
    }


    /**
     * Returns a list of all groups
     * @return List<Group>
     */
    public static List<Group> getAll() {
        return Group.find.where().orderBy("name").findList();
    }


    /**
     * Returns a map of all groups typically used in a select
     * @return Map<String,String>
     */
    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        List<Group> groups = Group.find.where().orderBy("name").findList();
        for(Group group : groups) {
            options.put(group.id.toString(), group.name);
        }
        return options;
    }

}
