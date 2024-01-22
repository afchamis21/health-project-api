package andre.chamis.healthproject.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "stripe")
public class StripeProperties {
    private String privateKey;
    private String webhookKey;
}
