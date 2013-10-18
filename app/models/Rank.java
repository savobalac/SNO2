package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Sav
 * Date: 18/10/13
 * Time: 14:20
 * Description: Model class that maps to DB table rank.
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Rank extends Model {

    // Instance variables
    @Id public Long                 id;
    public String                   name;


    /**
     * Generic query helper for entity analyst with id Long
     */
    public static Finder<Long, Rank> find = new Finder<Long, Rank>(Long.class, Rank.class);


    /**
     * Returns a map of all ranks typically used in a select
     * @return Map<String,String>
     */
    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        List<Rank> ranks = Rank.find.where().orderBy("name").findList();
        for(Rank rank : ranks) {
            options.put(rank.id.toString(), rank.name);
        }
        return options;
    }

}
