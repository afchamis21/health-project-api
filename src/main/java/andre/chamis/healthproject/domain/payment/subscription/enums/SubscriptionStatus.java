package andre.chamis.healthproject.domain.payment.subscription.enums;

import lombok.Getter;

/**
 * Represents the status of a user subscription.
 */
@Getter
public enum SubscriptionStatus {
    TRIALING("trialing"),
    ACTIVE("active"),
    INCOMPLETE("incomplete"),
    INCOMPLETE_EXPIRED("incomplete_expired"),
    PAST_DUE("past_due"),
    CANCELED("canceled"),
    UNPAID("unpaid"),
    UNKNOWN("unknown"),
    PAUSED("paused");

    private final String status;

    /**
     * Constructs a SubscriptionStatus enum with a specified status.
     *
     * @param status The status of the subscription.
     */
    SubscriptionStatus(String status) {
        this.status = status;
    }

    /**
     * Retrieves the SubscriptionStatus enum based on the provided status string.
     *
     * @param status The status string.
     * @return The SubscriptionStatus enum corresponding to the status string, or UNKNOWN if not found.
     */
    public static SubscriptionStatus fromString(String status) {
        for (SubscriptionStatus subscriptionStatus : SubscriptionStatus.values()) {
            if (subscriptionStatus.status.equalsIgnoreCase(status)) {
                return subscriptionStatus;
            }
        }

        return UNKNOWN;
    }
}
