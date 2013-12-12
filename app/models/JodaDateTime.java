package models;

import java.lang.annotation.*;
import utils.Utils;

/**
 * Annotation using the date time format dd-MMM-yyyy HH:mm.
 *
 * Date: 12/12/13
 * Time: 12:57
 *
 * @author      Sav Balac
 * @version     1.0
 */

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@play.data.Form.Display(name = "format.joda.datetime", attributes = { "format" })
public @interface JodaDateTime {
    String format() default Utils.DATETIME_FORMAT;
}
