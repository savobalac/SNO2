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
 * Time: 14:42
 * Description: Model class that maps to DB table desk.
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Desk extends Model {

    // Instance variables
    @Id public Long                 deskId;
    public String                   name;
    public Boolean                  coordinator;


    /**
     * Generic query helper for entity analyst with id Long
     */
    public static Finder<Long, Desk> find = new Finder<Long, Desk>(Long.class, Desk.class);


    /**
     * Returns a map of all desks typically used in a select
     * @return Map<String,String>
     */
    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        List<Desk> desks = Desk.find.where().orderBy("name").findList();
        for(Desk desk : desks) {
            options.put(desk.deskId.toString(), desk.name);
        }
        return options;
    }

}
