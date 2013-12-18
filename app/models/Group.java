package models;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder; // Import Finder as sometimes Play! shows compilation error "not found: type Finder"

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
 * @version     1.1
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
     * Returns a list of all groups.
     *
     * @return List<Group>  List of all groups.
     */
    public static List<Group> getAll() {
        return Group.find.where().orderBy("name").findList();
    }


    /**
     * Returns a map of all groups typically used in a select.
     *
     * @return Map<String,String>  All groups with key: id and value: name.
     */
    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        List<Group> groups = getAll();
        for(Group group : groups) {
            options.put(group.id.toString(), group.name);
        }
        return options;
    }


    /**
     * Gets all groups as JSON.
     *
     * @return ObjectNode  The groups as a JSON object node.
     */
    public static ObjectNode getAllAsJson() {
        List<Group> groups = getAll();
        ObjectNode result = Json.newObject();
        ArrayNode groupNodes = result.arrayNode();
        for (Group group : groups) {
            ObjectNode groupResult = group.toJson();
            groupNodes.add(groupResult);
        }
        result.put("groups", groupNodes);
        return result;
    }


    /**
     * Converts the group to JSON.
     * @return ObjectNode  The group as a JSON object node.
     */
    public ObjectNode toJson() {
        ObjectNode result = Json.newObject();
        result.put("id", id.toString());
        result.put("name", name);
        return result;
    }


}
