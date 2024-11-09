package andre.chamis.healthproject.domain.payment.subscription.repository;

import andre.chamis.healthproject.domain.payment.subscription.model.UserSubscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;


/**
 * Repository class for managing user subscriptions using JPA.
 */
@Repository
@RequiredArgsConstructor
public class UserSubscriptionRepository {
    private final UserSubscriptionJpaRepository subscriptionJpaRepository;

    /**
     * Saves a user subscription, updating the update date before saving.
     *
     * @param userSubscription The user subscription to be saved.
     * @return The saved user subscription.
     */
    public UserSubscription save(UserSubscription userSubscription) {
        userSubscription.setUpdateDt(LocalDateTime.now());
        return subscriptionJpaRepository.save(userSubscription);
    }

    /**
     * Retrieves a user subscription by its subscription ID.
     *
     * @param id The subscription ID.
     * @return An Optional containing the found user subscription, or empty if not found.
     */
    public Optional<UserSubscription> findBySubscriptionId(String id) {
        return subscriptionJpaRepository.findById(id);
    }

    /**
     * Deletes a user subscription by its subscription ID.
     *
     * @param id The subscription ID to delete.
     */
    public void deleteSubscriptionBySubscriptionId(String id) {
        subscriptionJpaRepository.deleteById(id);
    }
}
