package andre.chamis.healthproject.domain.subscription.repository;

import andre.chamis.healthproject.domain.subscription.model.UserSubscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserSubscriptionRepository {
    private final UserSubscriptionJpaRepository subscriptionJpaRepository;

    public UserSubscription save(UserSubscription userSubscription) {
        userSubscription.setUpdateDt(Date.from(Instant.now()));
        return subscriptionJpaRepository.save(userSubscription);
    }

    public Optional<UserSubscription> findBySubscriptionId(String id) {
        return subscriptionJpaRepository.findById(id);
    }

    public void deleteSubscriptionBySubscriptionId(String id) {
        subscriptionJpaRepository.deleteById(id);
    }
}
