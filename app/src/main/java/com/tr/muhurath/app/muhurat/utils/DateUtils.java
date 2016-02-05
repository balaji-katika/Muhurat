package com.tr.muhurath.app.muhurat.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utility for Date Operations
 * * Created by Balaji Katika (balaji.katika@gmail.com) on 1/31/16.
 */
public class DateUtils {
    private final static String DISPLAY_FORMAT = "hh-mm a";
    private final static String TWENTY_FOUR_INPUT_FORMAT = "HH:mm";
    private final static String TAG = DateUtils.class.getName();
    private final static String WEST = "West";
    private final static String EAST = "East";
    private final static String NORTH = "North";
    private final static String SOUTH = "South";
    /**
     * Get the day of the week
     * @param date - {@link Date} for which the day to be calculated
     * @return - index representing the day of the week starting with 1 for Sunday, 2 for Monday and so on
     */
    public static int getDayOfWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * Get the display format
     * @param index - Segment of the time frame
     * @param totalSegments - Total number of segments in the time frames (usually 8)
     * @param begin - Begining {@link Date}
     * @param close- Closing {@link Date}
     * @return - Pretty string
     */
    public static String getDisplayFormat(int index, int totalSegments, Date begin, Date close) {
        long diff = close.getTime() - begin.getTime();
        diff = diff / totalSegments;
        Date start = new Date(begin.getTime() + ((index - 1) * diff));
        Date end = new Date(begin.getTime() + (index * diff));
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat(DISPLAY_FORMAT);
        sb.append(sdf.format(start));
        sb.append(" - ");
        sb.append(sdf.format(end));
        return sb.toString();
    }

    /**
     * Convert the 24 Hour Format to 12 Hour
     * @param twentyFourHourFormat - Time in 24 hour format HH:mm
     * @return - Time in 12 hour format 12:18 PM
     */
    public static String get12HourFormat(String twentyFourHourFormat) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(TWENTY_FOUR_INPUT_FORMAT);
        Date date = null;
        try {
            date = inputFormat.parse(twentyFourHourFormat);
        }
        catch (ParseException ex) {
            Log.d(TAG, "get12HourFormat - Parsing Exception for " + twentyFourHourFormat);
            return twentyFourHourFormat;
        }
        SimpleDateFormat outputFormat = new SimpleDateFormat(DISPLAY_FORMAT);
        return outputFormat.format(date);
    }

    /**
     * Determines if it is Indian Timezone
     * @return - True/False
     */
    public static boolean isIndianTimeZone() {
        String timezone = TimeZone.getDefault().getID();
        for (String ist : AppConstants.IST_TIME_ZONE) {
            if (timezone.equalsIgnoreCase(ist)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Get the direction of the Shoolam for the given day
     * @param date - {@link Date} input
     * @return - Direction
     */
    public static String getShoolamDirection(Date date) {
        int dayOfWeek = getDayOfWeek(date);
        switch (dayOfWeek) {
            case 1:
                return WEST;
            case 2:
                return EAST;
            case 3:
                return NORTH;
            case 4:
                return NORTH;
            case 5:
                return SOUTH;
            case 6:
                return WEST;
            case 7:
                return EAST;
            default:
                //Assume Sunday
                return WEST;
        }
    }
}
