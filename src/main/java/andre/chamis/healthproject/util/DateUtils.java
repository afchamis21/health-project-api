package andre.chamis.healthproject.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateUtils {
    public static Date calculateOneWeekAgo() {
        Instant now = Instant.now();
        return Date.from(now.minus(7, ChronoUnit.DAYS));
    }
}
