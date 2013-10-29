package utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sav
 * Date: 16/10/13
 * Time: 13:23
 * To change this template use File | Settings | File Templates.
 */
public class Utils {

    // Constants
    public static final int MYSQL_TEXT_BYTES = 65535; // A text column is 2^16 bytes

    // Maximum upload file size of 10 Mb approximated from the MySQL max_allowed_packet, which is currently set to 25 Mb
    public static final int    MAX_FILE_SIZE = 10485760; // In bytes
    public static final String MAX_FILE_SIZE_STRING = MAX_FILE_SIZE / 1048576 + "Mb";

    public static final int LOG_LEVEL_NORMAL = 0;
    public static final int LOG_LEVEL_DEBUG = 1;
    public static final int LOG_LEVEL_VERBOSE = 2;

    public static final int LOG_TYPE_ERROR = -1;
    public static final int LOG_TYPE_GENERAL = 0;

    public static final String FLASH_KEY_INFO = "info";
    public static final String FLASH_KEY_ERROR = "error";
    public static final String FLASH_KEY_SUCCESS = "success";


    /**
     * Handles caught Exceptions
     * @param module
     * @param e
     */
    public static void eHandler(String module, Exception e) {
        String msg = "ERROR in " + module + ": " + e.getMessage() + " (" + e.getClass() + ")";
        addLogMsg(LOG_LEVEL_NORMAL, LOG_TYPE_ERROR, msg);
    }

    /**
     * Handles application errors
     * @param module
     * @param message
     */
    public static void errorHandler(String module, String message) {
        String msg = "ERROR in " + module + ": " + message;
        addLogMsg(LOG_LEVEL_NORMAL, LOG_TYPE_ERROR, msg);
    }

    /**
     * Handles application information
     * @param module
     * @param message
     */
    public static void logEvent(String module, String message) {
        String msg = module + ": " + message;
        addLogMsg(LOG_LEVEL_NORMAL, LOG_TYPE_GENERAL, msg);
    }

    /**
     * Logs an event
     * @param logLevel 0==Normal, 1==Debug, 2==Verbose
     * @param msg Description of event
     */
    public static void addLogMsg(int logLevel, int logType, String msg) {
        System.out.println("");
        System.out.println(Calendar.getInstance().getTime().toString() +   ": " + msg);
        System.out.println("");
    }

    /**
     * Returns a list of Integers based on start and end values
     * @param start    Start value
     * @param end      End value
     * @return List<Integer>
     */
    public static List<Integer> getIntegers(int start, int end) {
        int numIntegers = end - start + 1;
        List<Integer> list = new ArrayList<Integer>(numIntegers);
        for (int idx = 0; idx < numIntegers; idx++) {
            int value = start + idx;
            list.add(idx, new Integer(value));
        }
        return list;
    }

    /**
     * Returns titles to be typically used in a select
     * @return Map<String,String>
     */
    public static Map<String,String> optionsTitle() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();

        options.put(null, "Select a value");
        options.put("Mr.","Mr.");
        options.put("Mrs.","Mrs.");
        options.put("Miss","Miss");
        options.put("Ms.","Ms.");
        options.put("Dr.","Dr.");
        options.put("Prof.","Prof.");
        options.put("Rev.","Rev.");
        options.put("Other","Other");

        return options;
    }

    /**
     * Returns a friendly "Created on <date> at <time>" string
     * @param datetime Timestamp
     * @return String
     */
    public static String formatCreatedTimestamp(Timestamp datetime) {
        String result = "Created on " +
                        "" + new SimpleDateFormat("EEEE dd-MMM-yyyy").format(datetime) + " at " +
                        "" + new SimpleDateFormat("HH:mm").format(datetime);
        return result;
    }
}
