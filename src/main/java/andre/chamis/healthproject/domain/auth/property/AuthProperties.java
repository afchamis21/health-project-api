package andre.chamis.healthproject.domain.auth.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.temporal.ChronoUnit;

/**
 * Configuration class for JWT properties related to user and service authentication.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "auth.jwt")
public class AuthProperties {
    /**
     * JWT properties for user authentication.
     */
    private JwtProperties user;

    /**
     * JWT properties for service authentication.
     */
    private JwtProperties client;

    /**
     * Nested class representing JWT properties.
     */
    @Data
    public static class JwtProperties {
        /**
         * Configuration for access tokens.
         */
        private TokenConfig accessToken;

        /**
         * Configuration for refresh tokens.
         */
        private TokenConfig refreshToken;

        /**
         * Nested class representing token configuration.
         */
        @Data
        public static class TokenConfig {
            /**
             * Duration of the token validity period.
             */
            private int duration;

            /**
             * Unit of time for the token validity period (e.g., days, hours).
             */
            private ChronoUnit unit;

            /**
             * Encryption key used for securing the token.
             */
            private String encryptionKey;
        }
    }
}