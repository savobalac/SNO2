package controllers;

import play.*;
import play.api.mvc.*;
import play.api.mvc.Call;
import play.mvc.*;
import play.mvc.Controller;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

public class Application extends Controller {

    // Constants
    public static final int     RECORDS_PER_PAGE = 20;

    // Add a constant here when creating a new list page
    public static final int PAGE_TYPE_ANALYSTS =  1;

    public static Result index() {
        return ok(index.render("SNO2"));
    }

    public static Result test() {
        return ok(index.render("Test SNO2"));
    }

    /**
     * When a (pagination) link is clicked, return the required list page
     * @param pageType           The type of page, e.g. Analysts
     * @param page               The current page
     * @param sortBy             The sort column
     * @param sortOrder          Ascending or descending
     * @param filter1            The first search filter
     * @param filter2            The second search filter
     * @param filterNum          A numeric search filter
     * @return Call
     */
    public static Call getLink(int pageType, int page, String sortBy, String sortOrder,
                                            String filter1, String filter2, Long filterNum) {

        // Return the list page depending on the page type
        switch (pageType) {
            case PAGE_TYPE_ANALYSTS:
                return controllers.routes.Analysts.list(page, sortBy, sortOrder, filter1, filter2);
            default:
                return null;
        }

    }
}
