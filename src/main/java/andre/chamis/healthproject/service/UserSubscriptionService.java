package andre.chamis.healthproject.service;

import andre.chamis.healthproject.domain.payment.subscription.enums.SubscriptionStatus;
import andre.chamis.healthproject.domain.payment.subscription.model.UserSubscription;
import andre.chamis.healthproject.domain.payment.subscription.repository.UserSubscriptionRepository;
import andre.chamis.healthproject.exception.BadArgumentException;
import andre.chamis.healthproject.infra.request.response.ErrorMessage;
import andre.chamis.healthproject.util.DateUtils;
import com.stripe.model.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

/**
 * Manages user subscriptions and interacts with the repository for subscription-related operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserSubscriptionService {
    private final UserSubscriptionRepository subscriptionRepository;

    /**
     * Creates a subscription record in the database.
     *
     * @param subscription The subscription to create.
     */
    public void createSubscription(Subscription subscription) {
        createSubscription(subscription, false);
    }

    /**
     * Creates a subscription and adds it to the database.
     * If skipDbCheck is false, it checks if the subscription already exists in the database before creation.
     *
     * @param subscription The subscription to create.
     * @param skipDbCheck  A flag indicating whether to skip the database check.
     */
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
                .createDt(LocalDateTime.ofInstant(Instant.ofEpochMilli(subscription.getStartDate()), ZoneId.systemDefault()))
                .periodStart(DateUtils.getDateFromTimestamp(subscription.getCurrentPeriodStart()))
                .periodEnd(DateUtils.getDateFromTimestamp(subscription.getCurrentPeriodEnd()))
                .cancelAtPeriodEnd(subscription.getCancelAtPeriodEnd())
                .build();

        subscriptionRepository.save(userSubscription);
        log.info("Successfully added subscription to database [{}]", subscription.getId());
    }

    /**
     * Updates an existing subscription in the database.
     *
     * @param subscription The subscription to update.
     */
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

    /**
     * Deletes a subscription from the database.
     *
     * @param subscription The subscription to delete.
     */
    public void deleteSubscription(Subscription subscription) {
        log.info("Deleting subscription [{}}]", subscription.getId());

        subscriptionRepository.deleteSubscriptionBySubscriptionId(subscription.getId());
    }
}
