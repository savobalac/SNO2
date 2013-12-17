package models;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.Id;

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
 * @version     1.1
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
     * Returns a map of all statuses typically used in a select.
     *
     * @return Map<String,String>  A map of status id and status name.
     */
    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        List<Status> statuses = Status.find.where().orderBy("sortorder").findList();
        for(Status status : statuses) {
            options.put(status.statusId.toString(), status.statusName);
        }
        return options;
    }


    /**
     * Returns a map of statuses dependant on the user's group(s) typically used in a select.
     *
     * @return Map<String,String>  A map of status id and status name.
     */
    public static Map<String,String> options(User user) {

        // Get all status values
        Map<String,String> options = options();

        // If the user isn't an admin, manager or staff user,
        // remove status value Deleted" and those starting with "Removed"
        if (!user.isAdminOrManagerOrStaff()) {
            for (Iterator<Map.Entry<String,String>> it = options.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String,String> status = it.next();
                if (status.getValue().equals("Deleted") || status.getValue().startsWith("Removed")) {
                    it.remove();
                }
            }
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
