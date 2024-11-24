package andre.chamis.healthproject.interceptor;

import andre.chamis.healthproject.context.ServiceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.time.Instant;

/**
 * Interceptor responsible for managing the ServiceContext and logging execution details.
 */
@Slf4j
@Component
public class ServiceContextInterceptor implements HandlerInterceptor {
    /**
     * Pre-handle method that initializes the ServiceContext and logs the start of execution.
     *
     * @param request  The incoming HTTP request.
     * @param response The HTTP response.
     * @param handler  The handler for the request.
     * @return {@code true} to proceed with further processing.
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        String executionId = request.getHeader(ServiceContext.EXECUTION_ID_KEY);
        ServiceContext context = ServiceContext.getContext(executionId);
        context.setStartTime(Instant.now());

        response.setHeader(ServiceContext.EXECUTION_ID_KEY, context.getExecutionId());

        log.info(
                "Starting Execution of [{} - {}] at [{}]",
                request.getMethod(),
                request.getRequestURI(),
                context.getStartTime()
        );

        return true;
    }

    /**
     * After-completion method that logs execution details and clears the ServiceContext.
     *
     * @param request  The incoming HTTP request.
     * @param response The HTTP response.
     * @param handler  The handler for the request.
     * @param ex       The exception that occurred during processing (if any).
     */
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable Exception ex) {
        ServiceContext context = ServiceContext.getContext();
        context.setEndTime(Instant.now());
        Duration executionTime = Duration.between(context.getStartTime(), context.getEndTime());
        for (Exception exception : context.getExceptions()) {
            log.error("An exception of type [{}] has occurred during the execution of [{}]", exception.getClass().getSimpleName(), request.getRequestURI(), exception);
        }

        log.info(
                "Finishing execution of [{} - {}] at [{}]. Execution took [{}] ms",
                request.getMethod(),
                request.getRequestURI(),
                context.getEndTime(),
                executionTime.toMillis()
        );

        ServiceContext.clearContext();
    }
}
