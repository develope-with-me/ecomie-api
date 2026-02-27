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
    ResponseMessage<Subscription> unSubscribeUser(UUID subscriptionId);

    @Transactional
    ResponseMessage<Subscription> removeUserFromSession(UUID sessionId, UUID userId);

    Subscription update(UUID subscriptionId, @NotNull SubscriptionRequest subscriptionRequest);
    Subscription getSubscription(UUID subscriptionId);
    List<Subscription> getSubscriptions();
    List<Subscription> getSessionSubscription(UUID sessionId);

    ResponseMessage<Subscription> toggleBlock(UUID id);
}
