package me.jonahchin.musclebike.Utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by jonahchin on 2018-02-10.
 */

public class StringUtil {
    public static String getTitleDateFromMillis(long timestamp) {
        return new SimpleDateFormat("MMM d, yyyy - hh:mm aaa").format(new Date(timestamp));
    }

    public static String getHourMinuteSecondFromMillis(long timestamp) {

        if(timestamp > TimeUnit.HOURS.toMillis(1)){
            return String.format("%dh %dm %ds",
                    TimeUnit.MILLISECONDS.toHours(timestamp),
                    TimeUnit.MILLISECONDS.toMinutes(timestamp) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timestamp)),
                    TimeUnit.MILLISECONDS.toSeconds(timestamp) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timestamp)));
        }

        return String.format("%dm %ds",
                TimeUnit.MILLISECONDS.toMinutes(timestamp),
                TimeUnit.MILLISECONDS.toSeconds(timestamp) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timestamp))
        );
    }
}
