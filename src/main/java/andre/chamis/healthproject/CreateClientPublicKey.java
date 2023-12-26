package andre.chamis.healthproject;

import andre.chamis.healthproject.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreateClientPublicKey {
    public static void main(String[] args) {
        String publicKey = StringUtils.generateRandomString(16);

        log.info("A Chave Publica é: [{}]. Salve em algum lugar", publicKey);
    }
}
