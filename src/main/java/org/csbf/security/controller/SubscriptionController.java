package org.csbf.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csbf.security.service.SubscriptionService;
import org.csbf.security.utils.helperclasses.HelperDto;
import org.csbf.security.utils.helperclasses.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@SecurityRequirement(name = "ApiKey")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    @PostMapping(value = "/secure/user/subscribe")
    @Operation(summary = "Create Subscriptions", description = "Create new subscription", tags = { "USER", "SubscriptionController" })
    protected ResponseEntity<HelperDto.SubscriptionFullDto> createSubscription(@RequestBody HelperDto.SubscriptionCreateDto subscriptionCreateDto) {
        return new ResponseEntity<>(subscriptionService.subscribe(subscriptionCreateDto), HttpStatus.CREATED);
    }

    @PostMapping(value = "/secure/admin/subscribe/user/{userId}")
    @Operation(summary = "Subscribe User", description = "Add a user to a session", tags = { "ADMIN", "SubscriptionController" })
    public ResponseEntity<HelperDto.SubscriptionFullDto> subscribeUser(@PathVariable(name = "userId") UUID userId, @RequestBody HelperDto.SubscriptionCreateDto subscriptionCreateDto) {
        return new ResponseEntity<>(subscriptionService.subscribeUser(userId, subscriptionCreateDto), HttpStatus.PARTIAL_CONTENT);
    }

    @PostMapping(value = "/secure/admin/unsubscribe/{subscriptionId}/user")
    @Operation(summary = "Unsubscribe User", description = "Remove a user from a session", tags = { "ADMIN", "SubscriptionController" })
    public ResponseEntity<ResponseMessage> removeUserFromSession(@PathVariable(name = "subscriptionId") UUID subscriptionId, @RequestParam(name = "userId") UUID userId) {
        return new ResponseEntity<>(subscriptionService.unSubscribeUser(subscriptionId, userId), HttpStatus.PARTIAL_CONTENT);
    }

    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    @PostMapping(value = "/secure/admin/update-subscription/{subscriptionId}")
    @Operation(summary = "Update Subscription", description = "Update subscription", tags = { "ADMIN", "SubscriptionController" })
    public HelperDto.SubscriptionFullDto updateSubscription(@PathVariable(name = "subscriptionId") UUID subscriptionId, @RequestParam(name = "userId") UUID userId, @RequestBody HelperDto.SubscriptionCreateDto subscriptionCreateDto) {
        return subscriptionService.update(subscriptionId, userId, subscriptionCreateDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/secure/user/subscription/{subscriptionId}")
    @Operation(summary = "Get Subscription", description = "Get subscription", tags = { "USER", "SubscriptionController" })
    public HelperDto.SubscriptionFullDto getSubscription(@PathVariable(name = "subscriptionId") UUID subscriptionId) {
        return subscriptionService.getSubscription(subscriptionId);
    }

    @GetMapping(value = "/secure/admin/get-subscription")
    @Operation(summary = "Get Subscriptions", description = "Get all subscriptions", tags = { "ADMIN", "SubscriptionController" })
    public ResponseEntity<List<HelperDto.SubscriptionFullDto>> getSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getSubscriptions());
    }


}
