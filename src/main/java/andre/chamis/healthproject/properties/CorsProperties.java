package andre.chamis.healthproject.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for CORS (Cross-Origin Resource Sharing) properties.
 */
@Data
@Configuration
@ConfigurationProperties("auth.cors")
public class CorsProperties {
    /**
     * Array of allowed URIs for CORS.
     */
    private String[] allowedUris;
}
