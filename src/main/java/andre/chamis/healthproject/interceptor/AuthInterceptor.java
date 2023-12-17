package andre.chamis.healthproject.interceptor;


import andre.chamis.healthproject.context.ServiceContext;
import andre.chamis.healthproject.domain.auth.annotation.JwtAuthenticated;
import andre.chamis.healthproject.domain.auth.annotation.NonAuthenticated;
import andre.chamis.healthproject.domain.exception.UnauthorizedException;
import andre.chamis.healthproject.domain.response.ErrorMessage;
import andre.chamis.healthproject.domain.session.model.Session;
import andre.chamis.healthproject.service.JwtService;
import andre.chamis.healthproject.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

/**
 * Interceptor responsible for enforcing authentication and authorization for incoming requests.
 * It validates JWT tokens and checks for authentication requirements specified by annotations.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    private final JwtService jwtService;
    private final SessionService sessionService;
    private final AuthInterceptorProperties authInterceptorProperties;

    /**
     * Pre-handle method of the interceptor, responsible for enforcing authentication and authorization.
     *
     * @param request  The incoming HTTP request.
     * @param response The HTTP response.
     * @param handler  The handler method being executed.
     * @return True if the request is allowed, false otherwise.
     */
    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true; // Let fail for 404
        }

        if (authInterceptorProperties.getAllowedUris().contains(request.getRequestURI())) {
            return true;
        }

        AuthType authType = getRequestAuthType(handlerMethod);

        return switch (authType) {
            case JWT_TOKEN -> handleJwtAuthentication(request);
            case NON_AUTHENTICATED -> true;
        };
    }

    /**
     * Handles JWT authentication by validating the token, session, and user.
     *
     * @param request The incoming HTTP request.
     * @return True if authentication is successful, throws UnauthorizedException otherwise.
     */
    private boolean handleJwtAuthentication(HttpServletRequest request) {
        Optional<String> tokenFromHeaders = getTokenFromHeaders(request);

        String token = tokenFromHeaders.orElseThrow(UnauthorizedException::new);
        boolean isTokenValid = jwtService.validateAccessToken(token);
        if (!isTokenValid) {
            throw new UnauthorizedException(ErrorMessage.INVALID_JWT);
        }

        Long sessionId = jwtService.getSessionIdFromToken(token);
        Optional<Session> sessionOptional = sessionService.findSessionById(sessionId);
        Session session = sessionOptional.orElseThrow(() -> new UnauthorizedException(ErrorMessage.EXPIRED_SESSION));

        boolean isSessionValid = sessionService.validateSessionIsNotExpired(session);
        if (!isSessionValid) {
            sessionService.deleteSessionById(sessionId);
            throw new UnauthorizedException(ErrorMessage.EXPIRED_SESSION);
        }

        ServiceContext.getContext().setSessionId(sessionId);

        return true;
    }

    /**
     * Retrieves and validates the JWT token from the request headers.
     *
     * @param request The incoming HTTP request.
     * @return An Optional containing the token or an empty Optional.
     */
    private Optional<String> getTokenFromHeaders(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || authHeader.isBlank()) {
            return Optional.empty();
        }

        String[] authHeaderArray = authHeader.split(" ");
        if (!authHeaderArray[0].equalsIgnoreCase("bearer")) {
            return Optional.empty();
        }

        String token = authHeaderArray[1];
        return Optional.of(token);
    }

    /**
     * Enumeration representing the types of authentication for requests.
     */
    private enum AuthType {
        JWT_TOKEN,
        NON_AUTHENTICATED
    }

    /**
     * Determines the type of authentication required based on method annotations.
     *
     * @param handlerMethod The handler method being executed.
     * @return The type of authentication required for the method.
     */
    private AuthType getRequestAuthType(HandlerMethod handlerMethod) {
        if (handlerMethod.getMethod().isAnnotationPresent(JwtAuthenticated.class)) {
            return AuthType.JWT_TOKEN;
        }

        if (handlerMethod.getMethod().isAnnotationPresent(NonAuthenticated.class)) {
            return AuthType.NON_AUTHENTICATED;
        }

        if (handlerMethod.getBeanType().isAnnotationPresent(JwtAuthenticated.class)) {
            return AuthType.JWT_TOKEN;
        }

        if (handlerMethod.getBeanType().isAnnotationPresent(NonAuthenticated.class)) {
            return AuthType.NON_AUTHENTICATED;
        }

        return AuthType.JWT_TOKEN;
    }
}
