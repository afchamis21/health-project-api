package andre.chamis.healthproject.service;

import andre.chamis.healthproject.domain.payment.dto.CreateBillingPortalSessionRequest;
import andre.chamis.healthproject.domain.payment.dto.CreateBillingPortalSessionResponse;
import andre.chamis.healthproject.domain.payment.dto.CreateCheckoutSessionRequest;
import andre.chamis.healthproject.domain.payment.dto.GetCheckoutSessionResponse;
import andre.chamis.healthproject.domain.user.model.User;
import andre.chamis.healthproject.properties.StripeProperties;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class StripeService {
    private final StripeProperties stripeProperties;
    private final UserService userService;

    /**
     * Constructs a new {@code StripeService} with the required dependencies and initializes the Stripe API key.
     *
     * @param stripeProperties The configuration properties for Stripe.
     * @param userService      The service for user-related operations.
     */
    public StripeService(StripeProperties stripeProperties, UserService userService) {
        this.stripeProperties = stripeProperties;
        this.userService = userService;
        Stripe.apiKey = stripeProperties.getPrivateKey();

    }

    /**
     * Creates a checkout session using the provided request parameters.
     *
     * @param createCheckoutSessionRequest The request object containing parameters for creating a checkout session.
     * @return The response containing the ID of the created checkout session.
     * @throws StripeException If an error occurs during the Stripe API call.
     */
    public GetCheckoutSessionResponse createCheckoutSession(CreateCheckoutSessionRequest createCheckoutSessionRequest) throws StripeException {
        Optional<User> result = userService.getUserIfIsCustomer(createCheckoutSessionRequest.email());

        return result.isPresent()
                ? createCheckoutSessionForExistingCustomer(createCheckoutSessionRequest, result.get().getStripeClientId())
                : createCheckoutSessionWithEmailForNewCustomer(createCheckoutSessionRequest);
    }

    private GetCheckoutSessionResponse createCheckoutSessionWithEmailForNewCustomer(CreateCheckoutSessionRequest createCheckoutSessionRequest) throws StripeException {
        SessionCreateParams params = new SessionCreateParams.Builder()
                .setSuccessUrl(createCheckoutSessionRequest.successUrl())
                .setCancelUrl(createCheckoutSessionRequest.cancelUrl())
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setCustomerEmail(createCheckoutSessionRequest.email())
                .addLineItem(new SessionCreateParams.LineItem.Builder()
                        .setQuantity(1L)
                        .setPrice(createCheckoutSessionRequest.priceId())
                        .build()
                ).build();

        Session session = Session.create(params);
        return new GetCheckoutSessionResponse(session.getId());
    }

    private GetCheckoutSessionResponse createCheckoutSessionForExistingCustomer(CreateCheckoutSessionRequest createCheckoutSessionRequest, String stripeClientId) throws StripeException {
        SessionCreateParams params = new SessionCreateParams.Builder()
                .setSuccessUrl(createCheckoutSessionRequest.successUrl())
                .setCancelUrl(createCheckoutSessionRequest.cancelUrl())
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setCustomer(stripeClientId)
                .addLineItem(new SessionCreateParams.LineItem.Builder()
                        .setQuantity(1L)
                        .setPrice(createCheckoutSessionRequest.priceId())
                        .build()
                ).build();

        Session session = Session.create(params);
        return new GetCheckoutSessionResponse(session.getId());
    }

    /**
     * Creates a billing portal session using the provided request parameters.
     *
     * @param createBillingPortalSessionRequest The request object containing parameters for creating a billing portal session.
     * @return The response containing the URL of the created billing portal session.
     * @throws StripeException If an error occurs during the Stripe API call.
     */
    public CreateBillingPortalSessionResponse createBillingPortalSession(CreateBillingPortalSessionRequest createBillingPortalSessionRequest) throws StripeException {
        User user = userService.findCurrentUser();
        var params = new com.stripe.param.billingportal.SessionCreateParams.Builder()
                .setReturnUrl(createBillingPortalSessionRequest.returnUrl())
                .setCustomer(user.getStripeClientId())
                .build();

        var session = com.stripe.model.billingportal.Session.create(params);
        return new CreateBillingPortalSessionResponse(session.getUrl());
    }

    /**
     * Handles a Stripe webhook request by processing the payload and signature.
     *
     * @param payload         The payload of the webhook request.
     * @param signatureHeader The signature header of the webhook request.
     * @throws SignatureVerificationException If the Stripe signature cannot be verified.
     * @throws Exception                      If an error occurs during the webhook event processing.
     */
    public void handleWebhook(String payload, String signatureHeader) throws SignatureVerificationException, Exception {
        Event event = buildEventFromWebhookRequest(payload, signatureHeader);

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            log.error("Stripe Data Object is null. Error processing webhook request of type [{}]", event.getType());
            throw new Exception();
        }

        switch (event.getType()) {
            case "invoice.paid" -> handleInvoicePaidEvent((Invoice) stripeObject);
            case "invoice.payment_failed" -> handleInvoicePaymentFailed((Invoice) stripeObject);
            case "customer.created" -> handleCustomerCreatedEvent((Customer) stripeObject);
            case "customer.subscription.created" -> handleSubscriptionCreatedEvent((Subscription) stripeObject);
            case "customer.subscription.updated" -> handleSubscriptionUpdatedEvent((Subscription) stripeObject);
            case "customer.subscription.deleted" -> handleSubscriptionDeletedEvent((Subscription) stripeObject);
            default -> log.info("Got request of unhandled type [{}]", event.getType());
        }
    }

    /**
     * Builds a Stripe event from the webhook request payload and signature.
     *
     * @param payload         The payload of the webhook request.
     * @param signatureHeader The signature header of the webhook request.
     * @return The constructed Stripe event.
     * @throws SignatureVerificationException If the Stripe signature cannot be verified.
     */
    private Event buildEventFromWebhookRequest(String payload, String signatureHeader) throws SignatureVerificationException {
        return Webhook.constructEvent(
                payload,
                signatureHeader,
                stripeProperties.getWebhookKey()
        );
    }

    private void handleCustomerCreatedEvent(Customer customer) {
        log.info("Got webhook request of type customer.created. Customer: [{}]", customer.getEmail());
        userService.handleRegisterUserFromStripe(customer.getEmail(), customer.getId());
    }

    private void handleInvoicePaidEvent(Invoice invoice) {
        log.info("Got webhook request of type invoice.paid. Customer: [{}]", invoice.getCustomerEmail());
        userService.handleRegisterPayment(invoice.getCustomerEmail());
    }

    private void handleInvoicePaymentFailed(Invoice invoice) {
        log.info("Got webhook request of type invoice.payment_failed. Customer: [{}]", invoice.getCustomerEmail());
        userService.handlePaymentFailed(invoice.getCustomerEmail());
    }

    private void handleSubscriptionCreatedEvent(Subscription subscription) {
        log.info("Got webhook request of type customer.subscription.created. Customer: [{}]", subscription.getCustomer());
        userService.handleSubscriptionCreated(subscription);
    }

    private void handleSubscriptionUpdatedEvent(Subscription subscription) {
        log.info("Got webhook request of type customer.subscription.updated. Customer: [{}]", subscription.getCustomer());
        userService.handleSubscriptionUpdated(subscription);
    }

    private void handleSubscriptionDeletedEvent(Subscription subscription) {
        log.info("Got webhook request of type customer.subscription.deleted. Customer: [{}]", subscription.getCustomer());
        userService.handleSubscriptionDeleted(subscription);
    }
}
