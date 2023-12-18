package andre.chamis.healthproject.interceptor;


import andre.chamis.healthproject.context.ServiceContext;
import andre.chamis.healthproject.domain.exception.ForbiddenException;
import andre.chamis.healthproject.domain.response.ErrorMessage;
import andre.chamis.healthproject.domain.user.model.User;
import andre.chamis.healthproject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

/**
 * Interceptor to check whether a user has completed registration before allowing access to certain URIs.
 *
 * <p>The interceptor is responsible for checking the user's registration status and the requested URI.
 * It allows access if the user has completed registration or if the requested URI is in the list of allowed URIs.
 * If the user hasn't completed registration and is calling a protected URI, a ForbiddenException is thrown.
 * If no logged-in user is found, the request is allowed to proceed.
 * </p>
 *
 * <p>Allowed URIs without complete registration:
 * <ul>
 *     <li>/user/complete-registration</li>
 *     <li>/auth/login</li>
 *     <li>/auth/refresh</li>
 *     <li>/auth/logout</li>
 * </ul>
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IncompleteRegistrationInterceptor implements HandlerInterceptor {
    private static final List<String> URIS_ALLOWED_WITHOUT_COMPLETE_REGISTRATION = List.of(
            "/user",
            "/user/complete-registration",
            "/auth/login",
            "/auth/refresh",
            "/auth/logout"
    );

    private final UserService userService;

    /**
     * Pre-handle method to intercept and check whether the user has completed registration.
     *
     * @param request  The HTTP request.
     * @param response The HTTP response.
     * @param handler  The handler.
     * @return {@code true} if the user is allowed to proceed, {@code false} otherwise.
     * @throws ForbiddenException If the user hasn't completed registration and is calling a protected URI.
     */
    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) {
        Long sessionId = ServiceContext.getContext().getSessionId();

        if (sessionId == null) {
            log.debug("No session for request. Allowing request to procede");
            return true;
        }

        User user = userService.findCurrentUser();

        if (user.isRegistrationComplete() || isUriAllowedWithoutCompleteRegistration(request.getRequestURI())) {
            log.debug("User [{}] has completed registration or is calling an allowed URI. Allowing request to proceed", user);
            return true;
        }

        if (!user.isRegistrationComplete()) {
            log.debug("User [{}] hasn't completed registration, and is calling a protected URI. Stoppíng request with FORBIDDEN status code", user);
            throw new ForbiddenException(ErrorMessage.INCOMPLETE_REGISTRATION);
        }

        if (!user.isActive()) {
            log.debug("User is not active! Stoppíng request with FORBIDDEN status code");
            throw new ForbiddenException(ErrorMessage.INACTIVE_USER);
        }

        return false;
    }

    /**
     * Checks whether the given URI is in the list of allowed URIs without complete registration.
     *
     * @param uri The URI to check.
     * @return {@code true} if the URI is allowed without complete registration, {@code false} otherwise.
     */
    private boolean isUriAllowedWithoutCompleteRegistration(String uri) {
        return URIS_ALLOWED_WITHOUT_COMPLETE_REGISTRATION.stream()
                .anyMatch(allowedUri -> allowedUri.equalsIgnoreCase(uri));
    }
}
