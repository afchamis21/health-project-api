package andre.chamis.healthproject.domain.auth.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.temporal.ChronoUnit;

/**
 * Configuration properties for JSON Web Tokens (JWT) settings.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtProperties {
    /**
     * Configuration for the access token.
     */
    private TokenConfig accessToken;

    /**
     * Configuration for the access token.
     */
    private TokenConfig refreshToken;

    /**
     * Configuration for the access token.
     */
    @Data
    public static class TokenConfig {
        /**
         * Configuration for the access token.
         */
        private int duration;

        /**
         * Configuration for the access token.
         */
        private ChronoUnit unit;

        /**
         * The encryption key used for the token.
         */
        private String encryptionKey;
    }
}
