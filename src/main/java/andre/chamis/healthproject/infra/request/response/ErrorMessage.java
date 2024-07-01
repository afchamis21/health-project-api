package andre.chamis.healthproject.infra.request.response;

import lombok.Getter;

/**
 * Enumeration representing error messages.
 */
@Getter
public enum ErrorMessage {
    CAN_NOT_DELETE_ACTIVE_PATIENT("Para deletar um paciente, você deve primeiro desativá-lo!"),
    COLLABORATOR_IS_NOT_DEACTIVATED("Você deve desativar o usuário antes de fazer isso!"),
    EMAIL_ALREADY_REGISTERED("O email já está cadastrado"),
    EXPIRED_SESSION("Sua sessão expirou! Faça login novamente"),
    INACTIVE_PATIENT("Essa paciente está desativada!"),
    INACTIVE_USER("Seu usuário não está ativo!"),
    INCOMPLETE_REGISTRATION("Você ainda não completou seu cadastro! Siga as instruções em seu email"),
    INVALID_CREDENTIALS("Credenciais inválidas!"),
    INVALID_EMAIL("Email inválido!"),
    INVALID_GENDER("O gênero do paciente deve ser informado"),
    INVALID_JWT("Token inválido!"),
    INVALID_PASSWORD("Senha inválida!"),
    INVALID_PATIENT_ACCESS("Você não tem permissão para acessar esse paciente"),
    INVALID_PHONE("O telefone informado está inválido!"),
    INVALID_RG("O RG fornecido está inválido!"),
    INVALID_USERNAME("Nome de usuário inválido!"),
    MISSING_INFORMATION("A requisição foi enviada com informações faltando!"),
    MISSING_PATIENT_CONTACT_PHONE("O telefone de contato do paciente é obrigatório!"),
    MISSING_PATIENT_DATE_OF_BIRTH("A data de nascimento do paciente deve ser informada"),
    MISSING_PATIENT_NAME("O nome do paciente é obrigatório!"),
    MISSING_PATIENT_RG("O RG do paciente é obrigatório!"),
    MISSING_PATIENT_SURNAME("O sobrenome do paciente é obrigatório!"),
    MISSING_PRICE_ID("O parâmetro priceId é obrigatório!"),
    NO_SESSION("A sessão em contexto inválido!"),
    PAID_USER_ONLY("Apenas usuários pagantes podem fazer isso!"),
    PASSWORDS_DONT_MATCH("As senhas não conferem!"),
    PATIENT_NOT_FOUND("O paciente não foi encontrado!"),
    PATIENT_OWNERSHIP("Você deve ser responsável por esse paciente para fazer isso!"),
    SUBSCRIPTION_NOT_FOUND("Assinatura não encontrada!"),
    INTERNAL_SERVER_ERROR("Um erro desconhecido ocorreu! Contacte o time de suporte"),
    USER_ALREADY_COLLABORATOR("O usuário já está adicionado ao paciente!"),
    USER_ALREADY_HAS_OTP("Você já tem uma OTP, confira em seu email!"),
    USER_ALREADY_REGISTERED("Um usuário já está registrado com esse email!"),
    USER_NOT_FOUND("Usuário não encontrado!"),
    MISSING_PATIENT_CPF("O CPF do paciente é obrigatório!"),
    INVALID_CPF("O cpf fornecido está inválido!");
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
