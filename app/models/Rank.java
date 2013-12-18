package models;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder; // Import Finder as sometimes Play! shows compilation error "not found: type Finder"
import play.libs.Json;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Model class that maps to DB table rank.
 * Contains a method to get ranks.
 *
 * Date: 18/10/13
 * Time: 14:20
 *
 * @author      Sav Balac
 * @version     1.1
 */
@Entity
public class Rank extends Model {

    // Instance variables
    @Id public Long                 id;
    public String                   name;


    /**
     * Generic query helper for entity Rank.
     */
    public static Finder<Long, Rank> find = new Finder<Long, Rank>(Long.class, Rank.class);


    /**
     * Returns a list of all ranks.
     *
     * @return List<Rank>  List of all ranks.
     */
    public static List<Rank> getAll() {
        return Rank.find.where().orderBy("name").findList();
    }


    /**
     * Returns a map of all ranks typically used in a select.
     *
     * @return Map<String,String>  All ranks with key: id and value: name.
     */
    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        List<Rank> ranks = getAll();
        for(Rank rank : ranks) {
            options.put(rank.id.toString(), rank.name);
        }
        return options;
    }


    /**
     * Gets all ranks as JSON.
     *
     * @return ObjectNode  The ranks as a JSON object node.
     */
    public static ObjectNode getAllAsJson() {
        List<Rank> ranks = getAll();
        ObjectNode result = Json.newObject();
        ArrayNode rankNodes = result.arrayNode();
        for (Rank rank : ranks) {
            ObjectNode rankResult = rank.toJson();
            rankNodes.add(rankResult);
        }
        result.put("ranks", rankNodes);
        return result;
    }


    /**
     * Converts the rank to JSON.
     *
     * @return ObjectNode  The rank as a JSON object node.
     */
    public ObjectNode toJson() {
        ObjectNode result = Json.newObject();
        result.put("id", id.toString());
        if (name != null) { // Non-required fields may be null
            result.put("name", name);
        }
        return result;
    }


}
