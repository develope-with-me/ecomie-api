package org.csbf.security.service;

import org.csbf.security.utils.helperclasses.HelperDto;
import org.csbf.security.utils.helperclasses.ResponseMessage;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public interface SubscriptionService {
    HelperDto.SubscriptionFullDto subscribe(HelperDto.SubscriptionCreateDto subscriptionCreateDto);
    HelperDto.SubscriptionFullDto subscribeUser(UUID userId, HelperDto.SubscriptionCreateDto subscriptionCreateDto);
    ResponseMessage unSubscribeUser(UUID subscriptionId, UUID userId);
    HelperDto.SubscriptionFullDto update(UUID subscriptionId, UUID userId, HelperDto.@NotNull SubscriptionCreateDto subscriptionCreateDto);
    HelperDto.SubscriptionFullDto getSubscription(UUID subscriptionId);
    List<HelperDto.SubscriptionFullDto> getSubscriptions();
}
