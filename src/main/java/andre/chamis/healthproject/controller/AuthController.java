package andre.chamis.healthproject.controller;


import andre.chamis.healthproject.domain.auth.annotation.ClientAuthenticated;
import andre.chamis.healthproject.domain.auth.annotation.JwtAuthenticated;
import andre.chamis.healthproject.domain.auth.dto.RefreshTokensDTO;
import andre.chamis.healthproject.domain.auth.dto.TokensDTO;
import andre.chamis.healthproject.infra.request.response.ResponseMessage;
import andre.chamis.healthproject.infra.request.response.ResponseMessageBuilder;
import andre.chamis.healthproject.domain.user.dto.LoginDTO;
import andre.chamis.healthproject.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling user authentication and authorization.
 */
@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthorizationService authorizationService;

    /**
     * Authenticate a user and generate access and refresh tokens.
     *
     * @param loginDTO The DTO containing user credentials.
     * @return ResponseEntity containing the generated tokens and a status code.
     */
    @ClientAuthenticated
    @PostMapping("login")
    public ResponseEntity<ResponseMessage<TokensDTO>> login(@RequestBody LoginDTO loginDTO) {
        TokensDTO tokensDTO = authorizationService.authenticateUser(loginDTO);
        return ResponseMessageBuilder.build(tokensDTO, HttpStatus.CREATED);
    }

    /**
     * Refresh access and refresh tokens using a valid refresh token.
     *
     * @param refreshTokensDTO The DTO containing the refresh token.
     * @return ResponseEntity containing the refreshed tokens and a status code.
     */
    @ClientAuthenticated
    @PostMapping("refresh")
    public ResponseEntity<ResponseMessage<TokensDTO>> refresh(@RequestBody RefreshTokensDTO refreshTokensDTO) {
        TokensDTO tokensDTO = authorizationService.refreshUserTokens(refreshTokensDTO);
        return ResponseMessageBuilder.build(tokensDTO, HttpStatus.OK);
    }

    /**
     * Logout a user by invalidating their refresh token and session.
     *
     * @return ResponseEntity with a success message and status code.
     */
    @JwtAuthenticated
    @PostMapping("logout")
    public ResponseEntity<ResponseMessage<Void>> logout() {
        authorizationService.logout();
        return ResponseMessageBuilder.build(HttpStatus.OK);
    }
}
