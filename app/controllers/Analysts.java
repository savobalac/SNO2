package controllers;

import com.avaje.ebean.Page;
import models.Analyst;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;

/**
 * Created with IntelliJ IDEA.
 * User: Sav
 * Date: 16/10/13
 * Time: 13:35
 * To change this template use File | Settings | File Templates.
 */
public class Analysts extends Controller {

    /**
     * Displays a paginated list of analysts.
     *
     * @param page          Current page number (starts from 0)
     * @param sortBy        Column to be sorted
     * @param order         Sort order (either asc or desc)
     * @param filter        Filter applied on <column name>
     * @param search        Filter applied on <column names>
     * @return Result
     */
    public static Result list(int page, String sortBy, String order, String filter, String search) {
        Page<Analyst> pageSongs = Analyst.page(page, Application.RECORDS_PER_PAGE, sortBy, order, filter, search);
        return ok("Test list analysts page");
        //return ok(listAnalysts.render(pageSongs, sortBy, order, filter, search));
    }

}
