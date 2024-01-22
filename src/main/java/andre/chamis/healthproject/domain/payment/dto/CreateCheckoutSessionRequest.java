package andre.chamis.healthproject.domain.payment.dto;

public record CreateCheckoutSessionRequest(String priceId, String successUrl, String cancelUrl, String email) {
}
