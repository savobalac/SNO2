package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA.
 * User: Sav Balac
 * Date: 23/10/13
 * Time: 13:18
 * Description: Model class that maps to DB table deskanalyst.
 * To change this template use File | Settings | File Templates.
 */

@Entity
public class DeskAnalyst extends Model {

    @Id
    public Long         deskId;
    @Id
    public Long         analystId;
    //public byte[]       coordinator;


    /**
     * Generic query helper for entity DeskAnalyst with id Long
     */
    public static Finder<Long,DeskAnalyst> find = new Finder<Long,DeskAnalyst>(Long.class, DeskAnalyst.class);


}
