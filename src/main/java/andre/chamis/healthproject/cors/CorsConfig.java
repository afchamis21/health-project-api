package andre.chamis.healthproject.cors;

import andre.chamis.healthproject.properties.CorsProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for CORS (Cross-Origin Resource Sharing).
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class CorsConfig implements WebMvcConfigurer {
    private final CorsProperties corsProperties;

    /**
     * Configures CORS mappings.
     * <p>
     * This method sets up CORS (Cross-Origin Resource Sharing) policy for the application.
     * It allows requests from specified origins, with credentials, and defines allowed HTTP methods.
     * The allowed origins are specified by the CorsProperties bean provided during initialization.
     * Both global mapping for all endpoints (/**) and root mapping (/) are configured with the same CORS policy.
     *
     * @param registry CorsRegistry object to register CORS configurations.
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        String[] allowedUris = corsProperties.getAllowedUris();

        registry.addMapping("/**")
                .allowedOrigins(allowedUris)
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH");

        registry.addMapping("/")
                .allowedOrigins(allowedUris)
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH");
    }
}
