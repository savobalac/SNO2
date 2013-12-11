package models;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder; // Import Finder as sometimes Play! shows compilation error "not found: type Finder"
import play.libs.Json;
import utils.Utils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Model class that maps to DB table user.
 * Contains methods to list/save/update, check if a user is in a group, add/remove groups and authenticate the user.
 *
 * Date: 16/10/13
 * Time: 12:59
 *
 * @author      Sav Balac
 * @version     1.1
 */
@Entity
public class User extends Model {

    // Instance variables
    @Constraints.Required                     // A required constraint will ensure form fields are entered
    @Id public Long                 id;

    @Constraints.Required
    @Formats.NonEmpty                         // A non-empty format will convert spaces to null and ensure validation
    public String                   username;

    @Constraints.Required
    @Formats.NonEmpty
    public String                   password;

    @Constraints.Required
    @Formats.NonEmpty
    public String                   email;

    @Constraints.Required
    @Formats.NonEmpty
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
     * @param page          Page to display.
     * @param pageSize      Number of users per page.
     * @param sortBy        User property used for sorting.
     * @param order         Sort order (either or asc or desc).
     * @param filter        Filter applied on group name.
     * @param search        Search applied on fullname.
     * @return Page<User>   The page of users.
     */
    public static Page<User> page(int page, int pageSize, String sortBy, String order, String filter, String search) {

        // Search on fullname, otherwise filter on group name if it's set
        Page p = null;
        if (!search.isEmpty()) { // Search
            p = find.where()
                    .ilike("fullname", "%" + search + "%")
                    .orderBy(sortBy + " " + order)
                    .findPagingList(pageSize)
                    .getPage(page);
        } else {
            if (!filter.isEmpty()) { // Filter
                p = find.where()
                        .ilike("groups.name", "%" + filter + "%")
                        .orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .getPage(page);
            } else { // Get all records
                p = find.where()
                        .orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .getPage(page);
            }
        }
        return p;
    }


    /**
     * Saves or updates the user.
     *
     * @throws Exception  If there was a problem updating the DB.
     */
    public void saveOrUpdate() throws Exception {
        // The id should be 0 for a new record
        if (id <= 0) {
            // Check for duplicate username
            User user = User.find.where().eq("username", this.username).findUnique();
            if (user != null) {
                throw new Exception("Username already exists");
            }
            save();
        } else {
            update();
        }
    }


    /**
     * Checks if the user is an admin user. This static method is called from static controller methods.
     *
     * @param username  The username to be checked.
     * @return boolean  True if the user is an admin user, else returns false.
     */
    public static boolean isAdminUser(String username) {
        return find.where()
                .eq("username", username)
                .eq("groups.name", "Admin")
                .findRowCount() == 1;
    }


    /**
     * Checks if this user is an admin user, a manager or a staff member.
     *
     * @return boolean  True if the user is an admin user, a manager or a staff member, else returns false.
     */
    public boolean isAdminOrManagerOrStaff() {
        return isAdminOrManager() || isStaff();
    }


    /**
     * Checks if this user is an admin user or a manager.
     *
     * @return boolean  True if the user is an admin user, a manager or a staff member, else returns false.
     */
    public boolean isAdminOrManager() {
        return isAdmin() || isManager();
    }


    /**
     * Checks if this user is an admin user.
     *
     * @return boolean  True if the user is an admin user, else returns false.
     */
    public boolean isAdmin() {
        return find.where()
                .eq("username", username)
                .eq("groups.name", "Admin")
                .findRowCount() == 1;
    }


    /**
     * Checks if this user is a manager.
     *
     * @return boolean  True if the user is an admin user, else returns false.
     */
    public boolean isManager() {
        return find.where()
                .eq("username", username)
                .eq("groups.name", "Manager")
                .findRowCount() == 1;
    }


    /**
     * Checks if this user is a staff member.
     *
     * @return boolean  True if the user is a staff member, else returns false.
     */
    public boolean isStaff() {
        return find.where()
                .eq("username", username)
                .eq("groups.name", "Staff")
                .findRowCount() == 1;
    }


    /**
     * Returns the number of groups a user is assigned to (groups could be null).
     * @return int  The number of groups a user is assigned to.
     */
    public int getNumGroups() {
        if (groups != null) {
            return groups.size();
        } else {
            return 0;
        }
    }


    /**
     * Assigns a group to the user.
     *
     * @param group  The group to be added.
     * @throws       Exception  If there was a problem updating the DB.
     */
    public void addGroup(Group group) throws Exception {
        try {
            // Add the group if it hasn't been already, set the instance variables and update
            if (!(groups.contains(group))) {
                groups.add(group);
                saveManyToManyAssociations("groups"); // Update the database
                update();
            }
        }
        catch (Exception e) {
            Utils.eHandler("Analyst.addGroup()", e);
            throw e;
        }
    }


    /**
     * Deletes a group from the user.
     *
     * @param group  The group to be deleted.
     * @throws       Exception  If there was a problem updating the DB.
     */
    public void delGroup(Group group) throws Exception {
        try {
            // Remove the group if it exists, set the instance variables and update
            if (groups.contains(group)) {
                groups.remove(group);
                saveManyToManyAssociations("groups"); // Update the database
                update();
            } else {
                throw new Exception("Group: " + group.name + ", id: " + group.id + " not assigned to the user.");
            }
        }
        catch (Exception e) {
            Utils.eHandler("Analyst.delGroup()", e);
            throw e;
        }
    }


    /**
     * Deletes all groups from the user.
     *
     * @throws Exception  If there was a problem updating the DB.
     */
    public void delAllGroups() throws Exception {
        try {
            // Set the instance variable and update
            groups.clear();
            saveManyToManyAssociations("groups"); // Update the database
            update();
        }
        catch (Exception e) {
            Utils.eHandler("Analyst.delAllGroups()", e);
            throw e;
        }
    }


    /**
     * Checks the user's password. The hashed password is 64 characters long.
     *
     * @param  username  Username.
     * @param  password  Password to be hashed before checking.
     * @return User      The authenticated user.
     * @throws NoSuchAlgorithmException    If the hashing algorithm doesn't exist.
     */
    public static User authenticate(String username, String password) throws NoSuchAlgorithmException {
        return find.where().eq("username", username)
                           .eq("password", Utils.hashString(password)).findUnique();
    }


    /**
     * Gets all users as JSON.
     *
     * @return ObjectNode  The users as a JSON object node.
     */
    public static ObjectNode getAllUsersAsJson() {
        List<User> users = User.find.all();
        ObjectNode result = Json.newObject();
        ArrayNode userNodes = result.arrayNode();
        for (User user : users) {
            ObjectNode userResult = user.toJson();
            userNodes.add(userResult);
        }
        result.put("users", userNodes);
        return result;
    }

    /**
     * Gets the user's groups as JSON.
     *
     * @return ObjectNode  The user's groups as a JSON object node.
     */
    public ObjectNode getGroupsAsJson() {
        ObjectNode groupNodes = Json.newObject();
        groupNodes.put("groups", getGroupsAsJsonArray(groupNodes));
        return groupNodes;
    }


    /**
     * Converts the user and its groups to JSON. User-group is a many-many relationship.
     * Using Play's static toJson method results in a StackOverflow error (infinite recursion).
     *
     * @return ObjectNode  The user as a JSON object node.
     */
    public ObjectNode toJson() {
        ObjectNode userNode = Json.newObject();
        if (id == null) {
            return userNode;
        }
        userNode.put("id", id.toString());
        userNode.put("username", username);
        userNode.put("password", password);
        userNode.put("email", email);
        userNode.put("fullname", fullname);
        if (lastlogin != null) { // Non-required fields may be null
            userNode.put("lastlogin", Utils.formatTimestamp(lastlogin));
        }
        userNode.put("groups", getGroupsAsJsonArray(userNode));
        return userNode;
    }


    /**
     * Gets the user's groups as a JSON array node.
     *
     * @param  userNode   The user as a JSON ObjectNode
     * @return ArrayNode  The user's groups as a JSON array node.
     */
    private ArrayNode getGroupsAsJsonArray(ObjectNode userNode) {
        ArrayNode groupNodes = userNode.arrayNode();
        for (Group group : groups) {
            ObjectNode groupNode = group.toJson();
            groupNodes.add(groupNode);
        }
        return groupNodes;
    }


}
