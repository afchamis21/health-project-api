package andre.chamis.healthproject.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for generating random strings and OTPs.
 */
@Slf4j
public class StringUtils {
    private static final String ALLOWED_NUMBERS = "0123456789";
    private static final String ALLOWED_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String SPECIAL_CHARACTERS = "!@$%&#?";

    @Getter
    private static final Pattern phoneRegex = Pattern.compile("(\\(\\+?\\d{2}\\)|\\+?\\d{2})\\s?9?\\s?\\d{4}(-?|\\s?)\\d{4}");

    /**
     * Generates a random String with the specified length containing numbers and letters.
     *
     * @param length The length of the OTP.
     * @return The generated OTP.
     */
    public static String generateRandomString(int length) {
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
     * Checks if the given string contains uppercase letters.
     *
     * @param string The input string.
     * @return True if the string contains uppercase letters, false otherwise.
     */
    public static boolean containsUpperCaseLetters(String string) {
        if (string == null || string.isEmpty()) {
            return false;
        }

        for (String ch : string.split("")) {
            if (ch.equals(ch.toUpperCase())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the given string contains lowercase letters.
     *
     * @param string The input string.
     * @return True if the string contains lowercase letters, false otherwise.
     */
    public static boolean containsLowerCaseLetters(String string) {
        if (string == null || string.isEmpty()) {
            return false;
        }

        for (String ch : string.split("")) {
            if (ch.equals(ch.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the given string contains digits.
     *
     * @param string The input string.
     * @return True if the string contains digits, false otherwise.
     */
    public static boolean containsDigits(String string) {
        if (string == null || string.isEmpty()) {
            return false;
        }

        for (char ch : string.toCharArray()) {
            if (Character.isDigit(ch)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the given string contains special characters.
     *
     * @param string The input string.
     * @return True if the string contains special characters, false otherwise.
     */
    public static boolean containsSpecialChars(String string) {
        if (string == null || string.isEmpty()) {
            return false;
        }

        List<String> specialChars = List.of("!", "@", "#", "$", "%", "^", "&", "*");

        for (String ch : string.split("")) {
            if (specialChars.contains(ch)) {
                return true;
            }
        }

        return false;
    }
}
