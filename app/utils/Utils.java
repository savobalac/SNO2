package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sav
 * Date: 16/10/13
 * Time: 13:23
 * To change this template use File | Settings | File Templates.
 */
public class Utils {

    // Constants
    public static final int MYSQL_TEXT_BYTES = 65535; // 2^16

    public static final String FLASH_KEY_INFO = "info";
    public static final String FLASH_KEY_ERROR = "error";
    public static final String FLASH_KEY_SUCCESS = "success";

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

}
