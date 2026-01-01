package org.csbf.ecomie.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csbf.ecomie.service.SubscriptionService;
import org.csbf.ecomie.utils.helperclasses.HelperDomain.*;
import org.csbf.ecomie.utils.helperclasses.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/secure")
@SecurityRequirement(name = "ApiKey")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "SubscriptionController", description = "This controller contains endpoints for subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    @PostMapping(value = "/ecomiest/subscriptions")
    @Operation(summary = "Create Subscriptions", description = "Create new subscription", tags = { "ECOMIEST" })
    protected ResponseEntity<Subscription> createSubscription(@RequestBody SubscriptionRequest subscriptionRequest) {
        return new ResponseEntity<>(subscriptionService.subscribe(subscriptionRequest), HttpStatus.CREATED);
    }

    @PostMapping(value = "/admin/subscriptions/user/{userId}")
    @Operation(summary = "Subscribe User", description = "Add a user to a session", tags = { "ADMIN" })
    public ResponseEntity<Subscription> subscribeUser(@PathVariable(name = "userId") UUID userId, @RequestBody SubscriptionRequest subscriptionRequest) {
        return new ResponseEntity<>(subscriptionService.subscribeUser(userId, subscriptionRequest), HttpStatus.PARTIAL_CONTENT);
    }

    @DeleteMapping(value = "/admin/subscriptions/{id}")
    @Operation(summary = "Unsubscribe User", description = "Delete/Remove subscription", tags = { "ADMIN" })
    public ResponseEntity<ResponseMessage> removeUserFromSession(@PathVariable(name = "id") UUID id) {
        return new ResponseEntity<>(subscriptionService.unSubscribeUser(id), HttpStatus.PARTIAL_CONTENT);
    }

    @DeleteMapping(value = "/admin/subscriptions/{sessionId}/unsubscribe/user/{userId}")
    @Operation(summary = "Unsubscribe User", description = "Remove a user from a session", tags = { "ADMIN" })
    public ResponseEntity<ResponseMessage> removeUserFromSession(@PathVariable(name = "sessionId") UUID sessionId, @PathVariable(name = "userId") UUID userId) {
        return new ResponseEntity<>(subscriptionService.removeUserFromSession(sessionId, userId), HttpStatus.PARTIAL_CONTENT);
    }

    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    @PostMapping(value = "/admin/subscriptions/{id}")
    @Operation(summary = "Update Subscription", description = "Update subscription", tags = { "ADMIN" })
    public Subscription updateSubscription(@PathVariable(name = "id") UUID id, @RequestBody SubscriptionRequest subscriptionRequest) {
        return subscriptionService.update(id, subscriptionRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/ecomiest/subscriptions/{id}")
    @Operation(summary = "Get Subscription", description = "Get subscription", tags = { "ECOMIEST" })
    public Subscription getSubscription(@PathVariable(name = "id") UUID id) {
        return subscriptionService.getSubscription(id);
    }

    @GetMapping(value = "/admin/subscriptions")
    @Operation(summary = "Get Subscriptions", description = "Get all subscriptions", tags = { "ADMIN" })
    public ResponseEntity<List<Subscription>> getSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getSubscriptions());
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/ecomiest/subscriptions/sessions/{sessionId}")
    @Operation(summary = "Get A User's Subscription", description = "Get a user's subscription in a session", tags = { "ECOMIEST" })
    public Subscription getSessionSubscription(@PathVariable(name = "sessionId") UUID sessionId) {
        return subscriptionService.getSessionSubscription(sessionId);
    }


}
