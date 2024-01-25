package andre.chamis.healthproject.domain.subscription.enums;

import lombok.Getter;

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

    SubscriptionStatus(String status) {
        this.status = status;
    }

    public static SubscriptionStatus fromString(String status) {
        for (SubscriptionStatus subscriptionStatus : SubscriptionStatus.values()) {
            if (subscriptionStatus.status.equalsIgnoreCase(status)) {
                return subscriptionStatus;
            }
        }

        return UNKNOWN;
    }
}
