package andre.chamis.healthproject.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Utility class for date operations.
 */
public class DateUtils {

    /**
     * Calculates the date corresponding to one week ago.
     *
     * @return The date corresponding to one week ago.
     */
    public static Date calculateOneWeekAgo() {
        Instant now = Instant.now();
        return Date.from(now.minus(7, ChronoUnit.DAYS));
    }

    /**
     * Checks if a date is in the future.
     *
     * @param date The date to be checked.
     * @return true if the date is in the future, false otherwise.
     */
    public static boolean isDateInFuture(Date date) {
        return new Date().before(date);
    }

    /**
     * Checks if a date is in the past.
     *
     * @param date The date to be checked.
     * @return true if the date is in the past, false otherwise.
     */
    public static boolean isDateInPast(Date date) {
        return new Date().after(date);
    }

    /**
     * Converts a timestamp to a Date instance.
     *
     * @param timestamp The timestamp to be converted.
     * @return The Date instance corresponding to the timestamp.
     */
    public static Date getDateFromTimestamp(long timestamp) {
        return Date.from(getInstantFromTimestamp(timestamp));
    }

    /**
     * Converts a timestamp to an Instant instance.
     *
     * @param timestamp The timestamp to be converted.
     * @return The Instant instance corresponding to the timestamp.
     */
    public static Instant getInstantFromTimestamp(long timestamp) {
        return Instant.ofEpochMilli(getMillisFromTimestamp(timestamp));
    }

    /**
     * Converts a timestamp to milliseconds.
     *
     * @param timestamp The timestamp to be converted.
     * @return The value in milliseconds corresponding to the timestamp.
     */
    private static long getMillisFromTimestamp(long timestamp) {
        // Copiei esse código de https://www.baeldung.com/java-date-unix-timestamp
        if (timestamp >= 1E16 || timestamp <= -1E16) {
            return timestamp / 1_000_000;
        }
        if (timestamp >= 1E14 || timestamp <= -1E14) {
            return timestamp / 1_000;
        }
        if (timestamp >= 1E11 || timestamp <= -3E10) {
            return timestamp;
        }
        return timestamp * 1_000;
    }
}
