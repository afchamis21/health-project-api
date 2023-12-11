package andre.chamis.healthproject.interceptor;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration properties for the AuthInterceptor.
 * Defines a list of allowed URIs that bypass authentication and authorization checks.
 */
@Data
@Configuration
@ConfigurationProperties("auth")
public class AuthInterceptorProperties {
    /**
     * List of URIs that are allowed to bypass authentication and authorization checks.
     */
    private List<String> allowedUris;
}
