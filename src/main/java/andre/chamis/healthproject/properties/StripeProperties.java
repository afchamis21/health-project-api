package andre.chamis.healthproject.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Stripe properties.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "stripe")
public class StripeProperties {
    /**
     * The private key used for Stripe integration.
     */
    private String privateKey;

    /**
     * The webhook key used for Stripe integration.
     */
    private String webhookKey;
}
