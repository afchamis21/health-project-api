package andre.chamis.healthproject.service;

import andre.chamis.healthproject.domain.auth.model.OTP;
import andre.chamis.healthproject.domain.auth.repository.OtpRepository;
import andre.chamis.healthproject.domain.exception.ForbiddenException;
import andre.chamis.healthproject.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * Service class responsible for managing One-Time Password (OTP) related operations.
 */
@Service
@RequiredArgsConstructor
public class OtpService {
    private final OtpRepository otpRepository;
    private final EmailService emailService;

    private static final int OTP_LENGTH = 6;

    private static final String ALLOWED_CHARACTERS = "0123456789";

    /**
     * Generates a random OTP code of the specified length.
     *
     * @return The randomly generated OTP code.
     */
    private String generateCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < OtpService.OTP_LENGTH; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            char randomChar = ALLOWED_CHARACTERS.charAt(randomIndex);
            otp.append(randomChar);
        }

        return otp.toString();
    }

    /**
     * Generates a unique OTP for the specified user.
     *
     * @param user The user for whom the OTP is generated.
     * @return The generated OTP.
     * @throws ForbiddenException If the user already has an existing OTP.
     */
    private OTP generateOTP(User user) {
        if (otpRepository.existsByUserId(user.getUserId())) {
            throw new ForbiddenException("O usuário " + user.getEmail() + " já possui uma OTP!");
        }

        OTP otp = new OTP();
        otp.setUserId(user.getUserId());

        String code;

        do {
            code = generateCode();
        } while (otpRepository.existsByCode(code));

        otp.setCode(code);


        return otpRepository.save(otp);
    }

    /**
     * Handles the creation and delivery of an OTP for the specified user.
     *
     * @param user The user for whom the OTP is created.
     */
    public void handleCreateOTP(User user) {
        OTP otp = generateOTP(user);

        String message = """
                Olá aqui está a sua OTP para continuar o cadastro em nosso site!
                                
                {{OTP}}
                                
                Clique aqui para continuar o cadastro!
                """.replace("{{OTP}}", otp.getCode());

        emailService.sendMail(user.getEmail(), message, "Complete seu cadastro");
    }
}
