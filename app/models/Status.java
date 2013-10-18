package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Sav
 * Date: 18/10/13
 * Time: 14:32
 * Description: Model class that maps to DB table status.
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Status extends Model {

    // Instance variables
    @Id public Long                 statusId;
    public Long                     parent;
    public Long                     sortorder;
    public String                   statusName;


    /**
     * Generic query helper for entity analyst with id Long
     */
    public static Finder<Long, Status> find = new Finder<Long, Status>(Long.class, Status.class);


    /**
     * Returns a map of all statuses typically used in a select
     * @return Map<String,String>
     */
    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        List<Status> statuses = Status.find.where().orderBy("status_name").findList();
        for(Status status : statuses) {
            options.put(status.statusId.toString(), status.statusName);
        }
        return options;
    }

}
