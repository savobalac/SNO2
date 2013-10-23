package models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.*;

import play.data.validation.Constraints;
import play.db.ebean.Model;

/**
 * Created with IntelliJ IDEA.
 * User: Sav Balac
 * Date: 18/10/13
 * Time: 14:42
 * Description: Model class that maps to DB table desk.
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Desk extends Model {

    // Instance variables (Play! generates getters and setters)
    @Id public Long                 deskId;

    @Constraints.Required
    public String                   name;

    @Constraints.Required
    public Boolean                  coordinator;

    @ManyToMany
    @JoinTable(name="desk_analyst",
               joinColumns={@JoinColumn(name="desk_id", referencedColumnName="desk_id")},
               inverseJoinColumns={@JoinColumn(name="analyst_id", referencedColumnName="analyst_id")}
    ) public List<Analyst>          analysts;


    /**
     * Generic query helper for entity desk with id Long
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