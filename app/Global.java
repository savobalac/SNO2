import play.GlobalSettings;
import play.api.mvc.EssentialFilter;
import play.filters.csrf.CSRFFilter;

/**
 * Provides a global Cross Site Request Forgery (CSRF) filter to all requests.
 * All forms have a hidden CSRF token that will be checked.
 * If an unwelcome attempt is made to get or post data, the response is "Invalid token found in form body".
 *
 * Date:        10/12/13
 * Time:        11:32
 *
 * @author      Sav Balac
 * @version     1.0
 */
public class Global extends GlobalSettings {

    @Override
    public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[]{CSRFFilter.class};
    }

}
