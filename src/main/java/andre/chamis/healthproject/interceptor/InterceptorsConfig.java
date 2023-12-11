package andre.chamis.healthproject.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for registering interceptors in the Spring MVC framework.
 * Registers the AuthInterceptor and ServiceContextInterceptor for request handling.
 */
@Configuration
@RequiredArgsConstructor
public class InterceptorsConfig implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;
    private final ServiceContextInterceptor serviceContextInterceptor;

    /**
     * Adds the AuthInterceptor and ServiceContextInterceptor to the global list of interceptors.
     *
     * @param registry The registry for adding interceptors to handle requests.
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(serviceContextInterceptor);
        registry.addInterceptor(authInterceptor);
    }
}
