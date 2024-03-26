package andre.chamis.healthproject.domain.subscription.repository;

import andre.chamis.healthproject.domain.subscription.model.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing user subscriptions using JPA.
 */
@Repository
interface UserSubscriptionJpaRepository extends JpaRepository<UserSubscription, String> {
}
