package andre.chamis.healthproject.properties;

import java.time.temporal.ChronoUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "auth.otp")
public class OtpProperties {
    private OtpTokenConfig login; 
    private OtpTokenConfig forgotPassword; 

    @Data
    public static class OtpTokenConfig {
        private Long duration;
        private ChronoUnit unit;
    }
}
