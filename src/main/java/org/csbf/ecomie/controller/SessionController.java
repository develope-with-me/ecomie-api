package org.csbf.ecomie.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csbf.ecomie.service.SessionService;
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
@RequestMapping("/api/v1")
@SecurityRequirement(name = "ApiKey")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "SessionController", description = "This controller contains endpoints for sessions")
public class SessionController {

    private final SessionService sessionService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/secure/admin/sessions")
    @Operation(summary = "Create Session", description = "Create new session", tags = { "ADMIN" })
    public ResponseMessage createSession(@RequestBody Session session) {
        return sessionService.store(session);
    }

    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    @PutMapping(value = "/secure/admin/sessions/{id}")
    @Operation(summary = "Update Session", description = "Update session", tags = { "ADMIN" })
    public ResponseMessage updateSession(@PathVariable(name = "id") UUID id, @RequestBody Session session) {
        return sessionService.update(id, session);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/secure/user/sessions/{id}")
    @Operation(summary = "Get Session", description = "Get session", tags = { "USER, ADMIN" })
    public Session getSession(@PathVariable(name = "id") UUID id) {
        return sessionService.getSession(id);
    }

    @GetMapping(value = "/sessions")
    @Operation(summary = "Get Sessions", description = "Get all sessions", tags = { "UNAUTHENTICATED" })
    public ResponseEntity<List<Session>> getSessions() {
        return ResponseEntity.ok(sessionService.getSessions());
    }

    @GetMapping(value = "/secure/user/sessions")
    @Operation(summary = "Securely Get Sessions", description = "Securely get all sessions", tags = { "USER, ADMIN" })
    public ResponseEntity<List<Session>> securelyGetSessions() {
        return ResponseEntity.ok(sessionService.getSessions());
    }

    @PutMapping(value = "/secure/admin/sessions/{id}/status")
    @Operation(summary = "Update Session Status", description = "Update session's status. valid values {INACTIVE, ONGOING, PAUSED, ENDED}", tags = { "ADMIN" })
    public ResponseEntity<ResponseMessage> changeSessionStatus(@PathVariable(name = "id") UUID id, @RequestBody RequestProps props ) {
        return new ResponseEntity<>(sessionService.changeStatus(id, props.status()), HttpStatus.PARTIAL_CONTENT);
    }

    @PostMapping(value = "/secure/admin/sessions/{id}/assign/challenges")
    @Operation(summary = "Assign Challenges", description = "Assign challenges to session", tags = { "ADMIN" })
    public ResponseEntity<Session> assignChallenges(@PathVariable(name = "id") UUID id, @RequestBody RequestProps props) {
        return new ResponseEntity<>(sessionService.assignChallenges(id, props.ids()), HttpStatus.PARTIAL_CONTENT);
    }
    @PostMapping(value = "/secure/admin/sessions/{id}/remove/challenges")
    @Operation(summary = "Remove Challenges", description = "Remove challenges from session", tags = { "ADMIN" })
    public ResponseEntity<Session> removeChallenges(@PathVariable(name = "id") UUID id, @RequestBody RequestProps props) {
        return new ResponseEntity<>(sessionService.removeChallenges(id, props.ids()), HttpStatus.PARTIAL_CONTENT);
    }
    @PostMapping(value = "/secure/admin/sessions/{id}/assign/challenges/{challengeId}")
    @Operation(summary = "Assign Challenge", description = "Assign a challenge to session", tags = { "ADMIN" })
    public ResponseEntity<Session> assignChallenge(@PathVariable(name = "id") UUID id, @PathVariable(name = "challengeId") UUID challengeId) {
        return new ResponseEntity<>(sessionService.assignChallenge(id, challengeId), HttpStatus.PARTIAL_CONTENT);
    }
    @PostMapping(value = "/secure/admin/sessions/{id}/remove/challenges/{challengeId}")
    @Operation(summary = "Remove A Challenge", description = "Remove a challenge from session", tags = { "ADMIN" })
    public ResponseEntity<Session> removeChallenge(@PathVariable(name = "id") UUID id, @PathVariable(name = "challengeId") UUID challengeId) {
        return new ResponseEntity<>(sessionService.removeChallenge(id, challengeId), HttpStatus.PARTIAL_CONTENT);
    }

    @GetMapping(value = "/secure/admin/users/{userId}/sessions")
    @Operation(summary = "Get Sessions", description = "Get all user's sessions", tags = { "ADMIN" })
    public ResponseEntity<List<Session>> getUserSessions(@PathVariable(name = "userId") UUID userId) {
        return ResponseEntity.ok(sessionService.getUserSessions(userId));
    }

    @DeleteMapping(value = "/secure/admin/sessions/{id}")
    @Operation(summary = "Delete Session", description = "Delete session", tags = { "ADMIN" })
    public ResponseEntity<ResponseMessage> deleteSession(@PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(sessionService.deleteSession(id));
    }
}
