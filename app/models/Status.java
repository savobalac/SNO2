package models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Query;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder; // Import Finder as sometimes Play! shows compilation error "not found: type Finder"
import play.libs.Json;

/**
 * Model class that maps to DB table status.
 * Contains a method to get statuses.
 *
 * Date: 18/10/13
 * Time: 14:32
 *
 * @author      Sav Balac
 * @version     1.2
 */
@Entity
public class Status extends Model {

    // Instance variables (Play! generates getters and setters)
    @Id public Long                 statusId;
    public Long                     parent;
    public Long                     sortorder;
    public String                   statusName;


    /**
     * Generic query helper for entity Status.
     */
    public static Finder<Long, Status> find = new Finder<Long, Status>(Long.class, Status.class);


    /**
     * Returns a list of all statuses.
     *
     * @return List<Status>  A list of all statuses.
     */
    public static List<Status> getAll() {
        return Status.find.where().orderBy("sortorder").findList();
    }


    /**
     * Returns a list of all statuses, except "Deleted" and those starting with "Removed".
     *
     * @return List<Status>  A list of all statuses except "Deleted" and those starting with "Removed".
     */
    public static List<Status> getAllExceptDeletedRemoved() {
        Query<Status> query = Ebean.createQuery(Status.class);
        query.where(Expr.and(Expr.ne("statusName", "Deleted"),
                    Expr.not(Expr.startsWith("statusName", "Removed")))).orderBy("sortorder");
        return query.findList();
    }


    /**
     * Returns a map of all statuses typically used in a select.
     *
     * @return Map<String,String>  A map of status id and status name.
     */
    public static Map<String,String> options() {
        List<Status> statuses = getAll();
        return getStatusMap(statuses);
    }


    /**
     * Returns a map of statuses dependant on the user's group(s) typically used in a select.
     *
     * @param user  The logged-in user.
     * @return Map<String,String>  A map of status id and status name.
     */
    public static Map<String,String> options(User user) {
        List<Status> statuses = getStatuses(user);
        return getStatusMap(statuses);
    }


    /**
     * Returns a list of statuses dependant on the user's group(s).
     * All statuses for admin/manager/staff users, otherwise all except "Deleted" and those starting with "Removed".
     *
     * @param user  The logged-in user.
     * @return List<Status>  List of statuses as defined above.
     */
    private static List<Status> getStatuses(User user) {
        // Get all statuses if the user is an admin, manager or staff user,
        // Otherwise gets all except "Deleted" and those starting with "Removed"
        if (user.isAdminOrManagerOrStaff()) {
            return getAll();
        } else {
            return getAllExceptDeletedRemoved();
        }
    }


    /**
     * Returns a map of statuses typically used in a select.
     *
     * @param  statuses  The list of statuses.
     * @return Map<String,String>  A map of status id and status name.
     */
    private static Map<String,String> getStatusMap(List<Status> statuses) {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(Status status : statuses) {
            options.put(status.statusId.toString(), status.statusName);
        }
        return options;
    }


    /**
     * Returns whether the status name is "Deleted" or starts with "Removed".
     *
     * @return boolean  True if the status name is "Deleted" or starts with "Removed".
     */
    public boolean isDeletedOrRemoved() {
        return statusName.equals("Deleted") || statusName.startsWith("Removed");
    }


    /**
     * Gets statuses as JSON.
     *
     * @param user  The logged-in user.
     * @return ObjectNode  The statuses as a JSON object node.
     */
    public static ObjectNode getAllAsJson(User user) {
        List<Status> statuses = getStatuses(user);

        ObjectNode result = Json.newObject();
        ArrayNode statusNodes = result.arrayNode();
        for (Status status : statuses) {
            ObjectNode statusResult = status.toJson();
            statusNodes.add(statusResult);
        }
        result.put("statuses", statusNodes);
        return result;
    }


    /**
     * Converts the status to JSON.
     *
     * @return ObjectNode  The status as a JSON object node.
     */
    public ObjectNode toJson() {
        ObjectNode result = Json.newObject();
        result.put("statusId", statusId.toString());
        if (statusName != null) { // Non-required fields may be null
            result.put("statusName", statusName);
        }
        return result;
    }


}
