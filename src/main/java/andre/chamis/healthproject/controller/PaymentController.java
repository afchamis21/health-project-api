package andre.chamis.healthproject.controller;

import andre.chamis.healthproject.domain.auth.annotation.ClientAuthenticated;
import andre.chamis.healthproject.domain.auth.annotation.JwtAuthenticated;
import andre.chamis.healthproject.domain.auth.annotation.NonAuthenticated;
import andre.chamis.healthproject.domain.payment.dto.*;
import andre.chamis.healthproject.domain.response.ResponseMessage;
import andre.chamis.healthproject.domain.response.ResponseMessageBuilder;
import andre.chamis.healthproject.service.StripeService;
import andre.chamis.healthproject.service.UserService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@ClientAuthenticated
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final StripeService stripeService;
    private final UserService userService;

    /**
     * Creates a checkout session for processing a payment.
     *
     * @param createCheckoutSessionRequest The request object containing parameters for creating a checkout session.
     * @return ResponseEntity containing the response message and HTTP status code.
     * @throws StripeException If an error occurs during the Stripe API call.
     */
    @PostMapping("/create-checkout-session")
    public ResponseEntity<ResponseMessage<GetCheckoutSessionResponse>> createCheckoutSession(@RequestBody CreateCheckoutSessionRequest createCheckoutSessionRequest) throws StripeException {
        GetCheckoutSessionResponse response = stripeService.createCheckoutSession(createCheckoutSessionRequest);
        return ResponseMessageBuilder.build(response, HttpStatus.OK);
    }

    @GetMapping("/is-user-subscriber")
    public ResponseEntity<ResponseMessage<GetIsUserSubscriberResponse>> isUserSubscriber(@RequestParam String email) {
        GetIsUserSubscriberResponse response = userService.getIsUserSubscriber(email);
        return ResponseMessageBuilder.build(response, HttpStatus.OK);
    }

    /**
     * Creates a billing portal session for managing customer subscriptions.
     *
     * @param createBillingPortalSessionRequest The request object containing parameters for creating a billing portal session.
     * @return ResponseEntity containing the response message and HTTP status code.
     * @throws StripeException If an error occurs during the Stripe API call.
     */
    @JwtAuthenticated
    @PostMapping("/create-billing-portal-session")
    public ResponseEntity<ResponseMessage<CreateBillingPortalSessionResponse>> createBillingPortalSession(@RequestBody CreateBillingPortalSessionRequest createBillingPortalSessionRequest) throws StripeException {
        CreateBillingPortalSessionResponse response = stripeService.createBillingPortalSession(createBillingPortalSessionRequest);
        return ResponseMessageBuilder.build(response, HttpStatus.OK);
    }

    /**
     * Handles incoming Stripe webhook requests.
     *
     * @param payload         The payload of the webhook request.
     * @param signatureHeader The signature header of the webhook request.
     * @return ResponseEntity indicating the success or failure of processing the webhook.
     */
    @NonAuthenticated
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signatureHeader) {
        try {
            stripeService.handleWebhook(payload, signatureHeader);
        } catch (SignatureVerificationException ex) {
            log.error("Request failed due to bad 'Stripe-Signature' headers");
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }
}
