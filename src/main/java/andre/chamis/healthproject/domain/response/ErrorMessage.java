package andre.chamis.healthproject.domain.response;

import lombok.Getter;

/**
 * Enumeration representing error messages.
 */
@Getter
public enum ErrorMessage {
    EMAIL_ALREADY_REGISTERED("O email já está cadastrado"),
    EXPIRED_SESSION("Sua sessão expirou! Faça login novamente"),
    INACTIVE_USER("Seu usuário não está ativo!"),
    INCOMPLETE_REGISTRATION("Você ainda não completou seu cadastro! Siga as instruções em seu email"),
    INVALID_CREDENTIALS("Credenciais inválidas!"),
    INVALID_EMAIL("Email inválido!"),
    INVALID_JWT("Token inválido!"),
    INVALID_PASSWORD("Senha inválida!"),
    INVALID_USERNAME("Nome de usuário inválido!"),
    MISSING_INFORMATION("A requisição foi enviada com informações faltando!"),
    MISSING_PRICE_ID("O parâmetro priceId é obrigatório!"),
    NO_SESSION("A sessão em contexto inválido!"),
    PASSWORDS_DONT_MATCH("As senhas não conferem!"),
    SUBSCRIPTION_NOT_FOUND("Assinatura não encontrada!"),
    USER_ALREADY_HAS_OTP("Você já tem uma OTP, confira em seu email!"),
    USER_ALREADY_REGISTERED("Um usuário já está registrado com esse email!"),
    USER_NOT_FOUND("Usuário não encontrado!");

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
