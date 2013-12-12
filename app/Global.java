import java.text.ParseException;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import play.*;
import play.data.format.Formatters;

import play.GlobalSettings;
import play.api.mvc.EssentialFilter;
import play.filters.csrf.CSRFFilter;

/**
 * Provides a global Cross Site Request Forgery (CSRF) filter to all requests.
 * All forms have a hidden CSRF token that will be checked.
 * If an unwelcome attempt is made to get or post data, the response is "Invalid token found in form body".
 *
 * Also provides a formatter that works with the JodaDateTime annotation
 * to ensure valid datetime values from JSON pass form validation.
 *
 * Date:        10/12/13
 * Time:        11:32
 *
 * @author      Sav Balac
 * @version     1.0
 */
public class Global extends GlobalSettings {


    /*
     * Return the CSRF filter.
     */
    @Override
    public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[]{CSRFFilter.class};
    }


    /*
     * When the application starts, register the JodaDatTime annotation formatter.
     */
    @Override
    public void onStart(Application app) {
        Formatters.register(DateTime.class, new Formatters.AnnotationFormatter<models.JodaDateTime,DateTime>() {
            @Override
            public DateTime parse(models.JodaDateTime annotation, String input, Locale locale) throws ParseException {
                if (input == null || input.trim().isEmpty())
                    return null;

                if (annotation.format().isEmpty())
                    return new DateTime(Long.parseLong(input));
                else
                    return DateTimeFormat.forPattern(annotation.format()).withLocale(locale).parseDateTime(input);
            }

            @Override
            public String print(models.JodaDateTime annotation, DateTime time, Locale locale) {
                if (time == null)
                    return null;

                if (annotation.format().isEmpty())
                    return time.getMillis() + "";
                else
                    return time.toString(annotation.format(), locale);
            }

        });
    }


}
