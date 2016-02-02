package com.tr.muhurath.app.muhurat.kaal;

import java.util.Date;

/**
 * Service Interface for Kaal
 * Created by Balaji Katika (balaji.katika@gmail.com) on 1/31/16.
 */
public interface Kaal {
    public static int TOTAL_SEGMENTS_IN_DAY = 8;
    /**
     * Get the Kaal for the day
     * @param dayOfWeek - Day of the week. Zero based index i.e., 0 - sunday, 1 - Monday, 6 - Saturday
     * @return - Segment of the day (ranges from 1-8)
     */
    public int getMuhurat(int dayOfWeek);

    /**
     * Get the Muhurat for display purpose
     * @param sunRise - @{link Date} representing Sun Rise
     * @param sunSet- @{link Date} representing Sun Set
     * @return - String representing the start/end time of the Kaal
     */
    public String getMuhuratForDisplay(Date sunRise, Date sunSet);
}
