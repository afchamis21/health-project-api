package andre.chamis.healthproject.util;

import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;

/**
 * Utility class for generating random strings and OTPs.
 */
@Slf4j
public class StringUtils {
    private static final String ALLOWED_NUMBERS = "0123456789";
    private static final String ALLOWED_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String SPECIAL_CHARACTERS = "!@$%&#?";

    /**
     * Generates a random OTP (One-Time Password) with the specified length.
     *
     * @param length The length of the OTP.
     * @return The generated OTP.
     */
    public static String generateOTP(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();

        String ALLOWED_CHARACTERS = ALLOWED_LETTERS + ALLOWED_NUMBERS;

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            char randomChar = ALLOWED_CHARACTERS.charAt(randomIndex);
            otp.append(randomChar);
        }

        return otp.toString();
    }

    /**
     * Generates a random string with the specified length, including letters, numbers, and special characters.
     *
     * @param length The length of the random string.
     * @return The generated random string.
     */
    public static String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder();

        String ALLOWED_CHARACTERS = ALLOWED_LETTERS + ALLOWED_NUMBERS + SPECIAL_CHARACTERS;

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            char randomChar = ALLOWED_CHARACTERS.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }

        return stringBuilder.toString();
    }
}
