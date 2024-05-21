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
    USER_NOT_FOUND("Usuário não encontrado!"),
    WORKSPACE_OWNERSHIP("Você deve ser dono dessa workspace para fazer isso!"),
    WORKSPACE_NOT_FOUND("Workspace não encontrada!"),
    USER_ALREADY_MEMBER("O usuário já está adicionado à workspace!"),
    INACTIVE_WORKSPACE("Essa workspace está desativada!"),
    CAN_NOT_DELETE_ACTIVE_WORKSPACE("Para deletar uma workspace, você deve primeiro desativar ela!"),
    MISSING_WORKSPACE_NAME("O nome da workspace é obrigatório!"),
    INVALID_WORKSPACE_NAME("Esse nome da workspace é inválido! Ele deve ter de 4 a 50 caracteres"),
    UNKNOWN_ERROR("Um erro desconhecido ocorreu, tente novamente em alguns minutos"),
    INVALID_WORKSPACE_ACCESS("Você não tem permissão para acessar esse paciente"),
    PAID_USER_ONLY("Apenas usuários pagantes podem fazer isso!"),
    MEMBER_IS_DEACTIVATED("Este usuário foi temporariamente desativado dessa workspace. Contacte o administrador da workspace!"),
    MEMBER_IS_NOT_DEACTIVATED("Você deve desativar o usuário antes de fazer isso!"),
    INVALID_RG("O RG fornecido está inválido!"),
    PATIENT_ALREADY_REGISTERED("Um paciente já está cadastrado com esse RG!"),
    PATIENT_NOT_FOUND("O paciente não foi encontrado!"),
    MISSING_PATIENT_NAME("O nome do paciente é obrigatório!"),
    MISSING_PATIENT_RG("O RG do paciente é obrigatório!"),
    MISSING_PATIENT_CONTACT_PHONE("O telefone de contato do paciente é obrigatório!"),
    INVALID_PHONE("O telefone informado está inválido!"),
    INVALID_GENDER("O gênero do paciente deve ser informado"),
    MISSING_PATIENT_DATE_OF_BIRTH("A data de nascimento do paciente deve ser informada"),
    MISSING_PATIENT_SURNAME("O sobrenome do paciente é obrigatório!");

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
