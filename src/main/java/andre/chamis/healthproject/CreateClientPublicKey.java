package andre.chamis.healthproject;

import andre.chamis.healthproject.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
public class CreateClientPublicKey {
    public static void main(String[] args) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String publicKey = StringUtils.generateRandomString(16);

        log.info("A Chave Publica é: [{}]. Salve em algum lugar", publicKey);

        String hashedKey = bCryptPasswordEncoder.encode(publicKey);
        log.info("A chave hasheada é: [{}]. Salve no banco", hashedKey);
    }
}
