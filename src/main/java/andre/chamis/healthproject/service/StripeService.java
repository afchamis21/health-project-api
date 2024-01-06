package andre.chamis.healthproject.service;

import andre.chamis.healthproject.properties.StripeProperties;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Product;
import com.stripe.model.ProductCollection;
import com.stripe.param.ProductListParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeService {
    private final StripeProperties stripeProperties;

    @PostConstruct
    private void configureStripe() {
        log.info("No postConstruct do stripe service. Key [{}]", stripeProperties.getPrivateKey());
        Stripe.apiKey = stripeProperties.getPrivateKey();
    }

    public List<Product> getProducts() throws StripeException {
        ProductListParams params = ProductListParams.builder().setLimit(3L).build();

        ProductCollection products = Product.list(params);
        return products.getData();
    }
}
