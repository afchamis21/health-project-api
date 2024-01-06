package andre.chamis.healthproject.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.temporal.ChronoUnit;

/**
 * Configuration properties for user session settings.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "auth.session")
public class SessionProperties {
    /**
     * The duration of a user session.
     */
    private int duration;

    /**
     * The unit of time used for the session duration, such as minutes or hours.
     */
    private ChronoUnit unit;
}
