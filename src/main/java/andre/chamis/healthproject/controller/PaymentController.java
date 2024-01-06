package andre.chamis.healthproject.controller;

import andre.chamis.healthproject.domain.auth.annotation.NonAuthenticated;
import andre.chamis.healthproject.domain.response.ResponseMessage;
import andre.chamis.healthproject.domain.response.ResponseMessageBuilder;
import andre.chamis.healthproject.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@NonAuthenticated
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final StripeService stripeService;

    @GetMapping("/products")
    public ResponseEntity<ResponseMessage<List<Product>>> getProducts() throws StripeException {

        return ResponseMessageBuilder.build(stripeService.getProducts(), HttpStatus.OK);
    }
}
