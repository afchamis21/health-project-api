package andre.chamis.healthproject.domain.payment.subscription.model;


import andre.chamis.healthproject.domain.payment.subscription.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Represents user subscription entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "UserSubscription")
@Table(name = "subscriptions")
public class UserSubscription {
    @Id
    @Column(name = "subscription_id")
    private String subscriptionId;

    @Column(name = "stripe_client_id")
    private String stripeClientId;

    @Column(name = "cancel_at_period_end")
    private boolean cancelAtPeriodEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SubscriptionStatus status;

    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @Column(name = "update_dt")
    private LocalDateTime updateDt;

    @Column(name = "period_end")
    private Date periodEnd;

    @Column(name = "period_start")
    private Date periodStart;
}