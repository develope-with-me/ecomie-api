package org.csbf.ecomie.service;

import org.csbf.ecomie.utils.helperclasses.HelperDomain.*;
import org.csbf.ecomie.utils.helperclasses.ResponseMessage;
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
    Subscription subscribe(SubscriptionRequest subscriptionRequest);
    Subscription subscribeUser(UUID userId, SubscriptionRequest subscriptionRequest);
    ResponseMessage unSubscribeUser(UUID subscriptionId);

    @Transactional
    ResponseMessage removeUserFromSession(UUID sessionId, UUID userId);

    Subscription update(UUID subscriptionId, @NotNull SubscriptionRequest subscriptionRequest);
    Subscription getSubscription(UUID subscriptionId);
    List<Subscription> getSubscriptions();
    Subscription getSessionSubscription(UUID sessionId);
}
