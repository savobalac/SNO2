import java.text.ParseException;
import java.util.Locale;

import controllers.AbstractController;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import play.*;
import play.data.format.Formatters;

import play.GlobalSettings;
import play.api.mvc.EssentialFilter;
import play.filters.csrf.CSRFFilter;

import play.*;
import play.mvc.*;
import play.mvc.Http.*;
import play.libs.F.*;
import views.*;

import static play.mvc.Results.*;


/**
 * Provides a global Cross Site Request Forgery (CSRF) filter to all requests.
 * All forms have a hidden CSRF token that will be checked.
 * If an unwelcome attempt is made to get or post data, the response is "Invalid token found in form body".
 *
 * Also provides a formatter that works with the JodaDateTime annotation
 * to ensure valid datetime values from JSON pass form validation.
 *
 * Provides a custom Application Error and Page Not Found pages, and handles invalid request parameters and JSON bodies.
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


    /*
     * Return the custom application error page if an internal server error occurs.
     */
    public Promise<SimpleResult> onError(RequestHeader request, Throwable t) {
        String msg = "Internal server error";
        if (request.accepts("text/html")) {
            return Promise.<SimpleResult>pure(internalServerError(
                    views.html.applicationError.render(t)
            ));
        } else if (request.accepts("application/json") || request.accepts("text/json")) {
            return Promise.<SimpleResult>pure(internalServerError(
                    AbstractController.getErrorAsJson(msg + ": " + t)
            ));
        } else {
            return Promise.<SimpleResult>pure(badRequest(msg));
        }
    }


    /*
     * Return the custom page not found page if the URI is not found.
     */
    public Promise<SimpleResult> onHandlerNotFound(RequestHeader request) {
        String msg = "URL not found";
        if (request.accepts("text/html")) {
            return Promise.<SimpleResult>pure(notFound(
                    views.html.pageNotFound.render(request.uri())
            ));
        } else if (request.accepts("application/json") || request.accepts("text/json")) {
            return Promise.<SimpleResult>pure(notFound(
                    AbstractController.getErrorAsJson(msg + ": " + request.uri())
            ));
        } else {
            return Promise.<SimpleResult>pure(badRequest(msg));
        }
    }


    /*
     * Return a bad request message if it wasn't possible to bind the request parameters.
     */
    public Promise<SimpleResult> onBadRequest(RequestHeader request, String error) {
        String msg = "Was not possible to bind the request parameters.";
        if (request.accepts("text/html")) {
            return Promise.<SimpleResult>pure(notFound(
                    views.html.pageNotFound.render(request.uri() + ". " + msg)
            ));
        } else if (request.accepts("application/json") || request.accepts("text/json")) {
            return Promise.<SimpleResult>pure(badRequest(
                    AbstractController.getErrorAsJson(msg + " Please send a valid JSON body.")
            ));
        } else {
            return Promise.<SimpleResult>pure(badRequest(msg));
        }
    }


}
