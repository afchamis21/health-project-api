package andre.chamis.healthproject.domain.response;

import lombok.Getter;

/**
 * Enumeration representing error messages.
 */
@Getter
public enum ErrorMessage {
    USER_NOT_FOUND("Usuário não encontrado!"),
    INVALID_EMAIL("Email inválido!"),
    EMAIL_ALREADY_REGISTERED("O email já está cadastrado"),
    INCOMPLETE_REGISTRATION("Você ainda não completou seu cadastro! Siga as instruções em seu email"),
    INACTIVE_USER("Seu usuário não está ativo!"),
    INVALID_USERNAME("Nome de usuário inválido!"),
    INVALID_PASSWORD("Senha inválida!"),
    PASSWORDS_DONT_MATCH("As senhas não conferem!"),
    INVALID_JWT("Token inválido!"),
    EXPIRED_SESSION("Sua sessão expirou! Faça login novamente"),
    INVALID_CREDENTIALS("Credenciais inválidas!"),
    USER_ALREADY_HAS_OTP("Você já tem uma OTP, confira em seu email!"),
    MISSING_INFORMATION("A requisição foi enviada com informações faltando!"),
    NO_SESSION("A sessão em contexto inválido!");

    private final String message;

    /**
     * Constructs an error message enum with the specified message.
     *
     * @param message The error message.
     */
    ErrorMessage(String message) {
        this.message = message;
    }

}
