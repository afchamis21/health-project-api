package andre.chamis.healthproject.cors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CorsConfig implements WebMvcConfigurer {
    private final CorsProperties corsProperties;
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
