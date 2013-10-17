package models;

import com.avaje.ebean.Page;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import javax.persistence.*;
import java.sql.Timestamp;
import utils.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: Sav
 * Date: 16/10/13
 * Time: 12:59
 * Description: Model class that maps to DB table analyst.
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Analyst extends Model {

    // Instance variables
    @Id public Long                 analystId;
    public String                   salutation;
    public String                   firstname;
    public String                   lastname;
    public int                      statusId;
    public int                      rank;
    public String                   email;
    public String                   emailAlternate;
    public String                   paypalAccountEmail;
    public String                   mobile;
    public String                   phone;
    public String                   address1;
    public String                   address2;
    public String                   city;
    public String                   state;
    public String                   zip;
    public String                   country;
    public String                   countryOfResidence;
    public Boolean                  emailVerified;
    public Boolean                  phoneVerified;
    public String                   wikiUsername;
    public String                   highriseAccount;
    public Boolean                  contractSigned;

    @Lob @Column(name="position_description", length= Utils.MYSQL_TEXT_BYTES)
    public String                   position_description;

    @Lob @Column(name="biography", length= Utils.MYSQL_TEXT_BYTES)
    public String                   biography;

    @Lob @Column(name="academic", length= Utils.MYSQL_TEXT_BYTES)
    public String                   academic;

    @Lob @Column(name="notes", length= Utils.MYSQL_TEXT_BYTES)
    public String                   notes;

    public Timestamp                createOn;

    public String                   skype;
    @Lob @Column(name="profile_image", length= Utils.MYSQL_TEXT_BYTES)

    public String                   profileImage;
    @Lob @Column(name="cv_document", length= Utils.MYSQL_TEXT_BYTES)

    public String                   cvDocument;
    @Lob @Column(name="expertise", length= Utils.MYSQL_TEXT_BYTES)

    public String                   expertise;
    public int                      primaryDesk;


    /**
     * Generic query helper for entity analyst with id Long
     */
    public static Finder<Long,Analyst> find = new Finder<Long,Analyst>(Long.class, Analyst.class);


    /**
     * Returns a page of analysts.
     *
     * @param page          Page to display
     * @param pageSize      Number of analysts per page
     * @param sortBy        Analyst property used for sorting
     * @param order         Sort order (either or asc or desc)
     * @param filter        Filter applied on <column name>
     * @param search        Filter applied on <column names>
     * @return Page<Analyst>
     */
    public static Page<Analyst> page(int page, int pageSize, String sortBy, String order, String filter, String search) {

        // Search on lastname, otherwise filter
        Page p = null;
        if (!(search.equals("") || search == null)) {
            p = find.where()
                    .ilike("lastname", "%" + search + "%")
                    .orderBy(sortBy + " " + order)
                    .findPagingList(pageSize)
                    .getPage(page);
        } else {

            // Filter by lastname if it's set
            if (filter.equals("") || filter == null) {
                p = find.where()
                        .orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .getPage(page);
            } else {
                p = find.where()
                        .ilike("lastname", "%" + filter + "%")
                        .orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .getPage(page);
            }

        }
        return p;
    }

}