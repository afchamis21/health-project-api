package andre.chamis.healthproject.domain.subscription.model;


import andre.chamis.healthproject.domain.subscription.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
    private Date createDt;

    @Column(name = "update_dt")
    private Date updateDt;

    @Column(name = "period_end")
    private Date periodEnd;

    @Column(name = "period_start")
    private Date periodStart;
}