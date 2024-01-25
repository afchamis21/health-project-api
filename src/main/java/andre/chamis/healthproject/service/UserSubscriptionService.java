package andre.chamis.healthproject.service;

import andre.chamis.healthproject.domain.exception.BadArgumentException;
import andre.chamis.healthproject.domain.response.ErrorMessage;
import andre.chamis.healthproject.domain.subscription.enums.SubscriptionStatus;
import andre.chamis.healthproject.domain.subscription.model.UserSubscription;
import andre.chamis.healthproject.domain.subscription.repository.UserSubscriptionRepository;
import andre.chamis.healthproject.util.DateUtils;
import com.stripe.model.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSubscriptionService {
    private final UserSubscriptionRepository subscriptionRepository;

    public void createSubscription(Subscription subscription) {
        createSubscription(subscription, false);
    }

    private void createSubscription(Subscription subscription, boolean skipDbCheck) {
        log.info("Adding subscription [{}}] to database with user [{}]", subscription.getId(), subscription.getCustomer());

        if (!skipDbCheck) {
            log.info("Checking if subscription [{}] is already on database before creating it", subscription.getId());

            Optional<UserSubscription> result = subscriptionRepository.findBySubscriptionId(subscription.getId());

            if (result.isPresent()) {
                log.info("Subscription found! customer.subscription.updated webhook must've gotten here first!");
                updateSubscription(subscription);
                return;
            }

            log.info("Subscription [{}] not found on database! Creating a new one!", subscription.getId());
        }

        UserSubscription userSubscription = UserSubscription.builder()
                .subscriptionId(subscription.getId())
                .stripeClientId(subscription.getCustomer())
                .status(SubscriptionStatus.fromString(subscription.getStatus()))
                .createDt(DateUtils.getDateFromTimestamp(subscription.getStartDate()))
                .periodStart(DateUtils.getDateFromTimestamp(subscription.getCurrentPeriodStart()))
                .periodEnd(DateUtils.getDateFromTimestamp(subscription.getCurrentPeriodEnd()))
                .cancelAtPeriodEnd(subscription.getCancelAtPeriodEnd())
                .build();

        subscriptionRepository.save(userSubscription);
        log.info("Successfully added subscription to database [{}]", subscription.getId());
    }

    public void updateSubscription(Subscription subscription) {
        log.info("Updating subscription [{}}]", subscription.getId());

        Optional<UserSubscription> result = subscriptionRepository.findBySubscriptionId(subscription.getId());
        if (result.isEmpty()) {
            log.info("Subscription [{}] not found on database! Creating a new one instead!", subscription.getId());
            createSubscription(subscription, true);
            return;
        }

        UserSubscription userSubscription = result.orElseThrow(() -> new BadArgumentException(ErrorMessage.SUBSCRIPTION_NOT_FOUND));

        userSubscription.setPeriodStart(DateUtils.getDateFromTimestamp(subscription.getCurrentPeriodStart()));
        userSubscription.setPeriodEnd(DateUtils.getDateFromTimestamp(subscription.getCurrentPeriodEnd()));
        userSubscription.setStatus(SubscriptionStatus.fromString(subscription.getStatus()));
        userSubscription.setCancelAtPeriodEnd(subscription.getCancelAtPeriodEnd());

        subscriptionRepository.save(userSubscription);

        log.info("Subscription [{}}] updated!", subscription.getId());
    }

    public void deleteSubscription(Subscription subscription) {
        log.info("Deleting subscription [{}}]", subscription.getId());

        subscriptionRepository.deleteSubscriptionBySubscriptionId(subscription.getId());
    }
}
