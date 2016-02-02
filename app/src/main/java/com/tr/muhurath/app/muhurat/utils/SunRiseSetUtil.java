package com.tr.muhurath.app.muhurat.utils;

import android.util.Log;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;

import java.util.Calendar;
import java.util.Date;

/**
 * Utility for determining Sun Rise/Sun Set
 * * Created by Balaji Katika (balaji.katika@gmail.com) on 1/31/16.
 */
public class SunRiseSetUtil {
    private static String TAG = SunRiseSetUtil.class.getName();

    /**
     * Get the SunRise for the given date
     * @param date - input date
     * @return - Date with time specifying the Sun Rise
     */
    public static Date getSunRise(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY,06);
        cal.set(Calendar.MINUTE,00);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND, 0);
        Date ret = cal.getTime();
        return ret;
    }

    /**
     * Get the SunRise for the given date and location
     * @param date - input date
     * @return - Date with time specifying the Sun Rise
     */
    public static Date getSunRiseLocationBased(Date date, SunriseSunsetCalculator sunriseSunsetCalculator) {
        return getOfficialSunSetRise_internal(date, sunriseSunsetCalculator, true);
    }

    /**
     * Get the Sun Set for the given date and location
     * @param date - input date
     * @return - Date with time specifying the Sun Rise
     */
    public static Date getSunSetLocationBased(Date date, SunriseSunsetCalculator sunriseSunsetCalculator) {
        return getOfficialSunSetRise_internal(date, sunriseSunsetCalculator, false);
    }

    private static Date getOfficialSunSetRise_internal(Date date, SunriseSunsetCalculator sunriseSunsetCalculator, boolean isSunRise) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (isSunRise) {
            //Log only once
            logDetails(date, sunriseSunsetCalculator, calendar);
        }
        String official = null;
        if (isSunRise) {
            official = sunriseSunsetCalculator.getOfficialSunriseForDate(calendar);
        }
        else {
            official = sunriseSunsetCalculator.getOfficialSunsetForDate(calendar);
        }

        String official_arr[] = official.split(AppConstants.SUNRISE_SUNSET_TIME_DELIMITER);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(official_arr[0]));
        cal.set(Calendar.MINUTE, Integer.valueOf(official_arr[1]));
        cal.set(Calendar.SECOND,00);
        cal.set(Calendar.MILLISECOND, 0);
        Date ret = cal.getTime();
        return ret;
    }

    /**
     * Log the necessary information
     * @param calculator - {@link SunriseSunsetCalculator} instance
     * @param calendar - {@link Calendar} instance
     */
    private static void logDetails(Date date, SunriseSunsetCalculator calculator, Calendar calendar) {
        Log.d(TAG, date.toString());
        Log.d(TAG, "Astronomical Sun Rise = " + calculator.getAstronomicalSunriseForDate(calendar));
        Log.d(TAG, "Astronomical Sun Set = " + calculator.getAstronomicalSunsetForDate(calendar));
        Log.d(TAG, "Civil Sun Rise = " + calculator.getCivilSunriseForDate(calendar));
        Log.d(TAG, "Civil Sun Set = " + calculator.getCivilSunsetForDate(calendar));
        Log.d(TAG, "Official Sun Rise = " + calculator.getOfficialSunriseForDate(calendar));
        Log.d(TAG, "Official Sun Set = " + calculator.getOfficialSunsetForDate(calendar));
        Log.d(TAG, "Nautical Sun Rise = " + calculator.getNauticalSunriseForDate(calendar));
        Log.d(TAG, "Nautical Sun Set = " + calculator.getNauticalSunsetForDate(calendar));
    }

    /**
     * Get the SunSet for the given date
     * @param date - input date
     * @return - Date with time specifying the Sun Set
     */
    public static Date getSunSet(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY,18);
        cal.set(Calendar.MINUTE,00);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND, 0);
        Date ret = cal.getTime();
        return ret;
    }
}
