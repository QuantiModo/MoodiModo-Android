package com.quantimodo.android.sdk;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class that has some utils for making requests to Quantimodo API
 */
public class Utils {

    /***
     * Takes a date and return a string needed to make request to server
     * @param date the date to convert
     * @return the date in format yyyy-MM-ddThh:mm:ss, ie: 2015-10-10T00:00:00
     */
    public static String formatDateToString(final Date date){
        return new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.US).format(date);
    }
}
