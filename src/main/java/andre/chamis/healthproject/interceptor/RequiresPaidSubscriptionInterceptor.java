package andre.chamis.healthproject.interceptor;

import andre.chamis.healthproject.domain.auth.annotation.RequiresPaidSubscription;
import andre.chamis.healthproject.domain.user.model.User;
import andre.chamis.healthproject.exception.ForbiddenException;
import andre.chamis.healthproject.infra.request.response.ErrorMessage;
import andre.chamis.healthproject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor responsible for restricting access to endpoints that require a paid subscription.
 */
@Component
@RequiredArgsConstructor
public class RequiresPaidSubscriptionInterceptor implements HandlerInterceptor {
    private final UserService userService;


    /**
     * Pre-handle method that checks if the user has an active paid subscription
     * before allowing access to certain endpoints.
     *
     * <p>If the handler method is annotated with {@link RequiresPaidSubscription},
     * this interceptor verifies if the current user has an active subscription.
     * If not, the request is blocked.</p>
     *
     * @param request  The incoming HTTP request.
     * @param response The HTTP response.
     * @param handler  The handler for the request.
     * @return {@code true} if the user is allowed to proceed, {@code false} otherwise.
     * @throws Exception if an error occurs during processing.
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // Skip if the handler is not a method (e.g., static resources)
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true; // Proceed to the next interceptor or controller for non-handler methods
        }

        // Check if the method is annotated with @RequiresPaidSubscription
        if (!handlerMethod.getMethod().isAnnotationPresent(RequiresPaidSubscription.class)) {
            return true; // Proceed if no annotation is present
        }

        // Verify if the current user has an active paid subscription
        User currentUser = userService.findCurrentUser();
        boolean isPaidUser = currentUser.isPaymentActive();

        // Deny access if the user does not have an active subscription
        if (!isPaidUser) {
            throw new ForbiddenException(ErrorMessage.PAID_USER_ONLY);
        }

        return true;
    }
}
