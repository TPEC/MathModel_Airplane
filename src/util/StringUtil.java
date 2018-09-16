package util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtil {
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("DD HH:mm");

    public static String formatDate(long time) {
        return DATE_FORMAT.format(new Date(time));
    }
}
