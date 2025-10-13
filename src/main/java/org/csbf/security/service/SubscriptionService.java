package org.csbf.security.service;

import org.csbf.security.utils.helperclasses.HelperDomain.*;
import org.csbf.security.utils.helperclasses.ResponseMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
public interface SubscriptionService {
    Subscription subscribe(SubscriptionCreateDto subscriptionCreateDto);
    Subscription subscribeUser(UUID userId, SubscriptionCreateDto subscriptionCreateDto);
    ResponseMessage unSubscribeUser(UUID subscriptionId);

    @Transactional
    ResponseMessage removeUserFromSession(UUID sessionId, UUID userId);

    Subscription update(UUID subscriptionId, @NotNull SubscriptionCreateDto subscriptionCreateDto);
    Subscription getSubscription(UUID subscriptionId);
    List<Subscription> getSubscriptions();
    Subscription getSessionSubscription(UUID sessionId);
}
