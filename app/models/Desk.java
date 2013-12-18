package models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.*;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder; // Import Finder as sometimes Play! shows compilation error "not found: type Finder"
import play.libs.Json;

/**
 * Model class that maps to DB table desk.
 * Contains methods to get desks.
 *
 * Date: 18/10/13
 * Time: 14:42
 *
 * @author      Sav Balac
 * @version     1.2
 */
@Entity
public class Desk extends Model {

    // Instance variables (Play! generates getters and setters)
    @Id public Long                 deskId;

    @Constraints.Required
    public String                   name;

    public Boolean                  coordinator;

    @ManyToMany @JoinTable(name="deskanalyst",
        joinColumns={@JoinColumn(name="desk_id", referencedColumnName="desk_id")},
        inverseJoinColumns={@JoinColumn(name="analyst_id", referencedColumnName="analyst_id")}
    ) public List<Analyst>          analysts; // The columns are in deskanalyst, the referenced columns are in analyst and desk


    /**
     * Generic query helper for entity Desk.
     */
    public static Finder<Long, Desk> find = new Finder<Long, Desk>(Long.class, Desk.class);


    /**
     * Returns a list of all desks.
     *
     * @return List<Desk>  A list of all desks.
     */
    public static List<Desk> getAll() {
        return Desk.find.where().orderBy("name").findList();
    }


    /**
     * Returns a map of all desks typically used in a select.
     *
     * @return Map<String,String>  All desks with key: id and value: name.
     */
    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        List<Desk> desks = getAll();
        for(Desk desk : desks) {
            options.put(desk.deskId.toString(), desk.name);
        }
        return options;
    }


    /**
     * Gets all desks as JSON.
     *
     * @return ObjectNode  The desks as a JSON object node.
     */
    public static ObjectNode getAllAsJson() {
        List<Desk> desks = getAll();
        ObjectNode result = Json.newObject();
        ArrayNode deskNodes = result.arrayNode();
        for (Desk desk : desks) {
            ObjectNode deskResult = desk.toJson();
            deskNodes.add(deskResult);
        }
        result.put("desks", deskNodes);
        return result;
    }


    /**
     * Converts the desk to JSON.
     *
     * @return ObjectNode  The desk as a JSON object node.
     */
    public ObjectNode toJson() {
        ObjectNode result = Json.newObject();
        result.put("deskId", deskId.toString());
        result.put("name", name);
        return result;
    }


}
