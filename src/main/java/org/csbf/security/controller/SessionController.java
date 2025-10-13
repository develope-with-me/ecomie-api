package org.csbf.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csbf.security.service.SessionService;
import org.csbf.security.utils.helperclasses.HelperDomain.*;
import org.csbf.security.utils.helperclasses.ResponseMessage;
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
@Tag(name = "SessionController", description = "This controller contains endpoints for sessionEntities")
public class SessionController {

    private final SessionService sessionService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/admin/sessions")
    @Operation(summary = "Create SessionEntity", description = "Create new sessionEntity", tags = { "ADMIN" })
    protected ResponseMessage createSession(@RequestBody Session session) {
        return sessionService.store(session);
    }

    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    @PostMapping(value = "/admin/sessions/{sessionId}")
    @Operation(summary = "Update SessionEntity", description = "Update sessionEntity", tags = { "ADMIN" })
    public ResponseMessage updateSession(@PathVariable(name = "sessionId") UUID sessionId, @RequestBody Session session) {
        return sessionService.update(sessionId, session);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/user/sessions/{sessionId}")
    @Operation(summary = "Get SessionEntity", description = "Get sessionEntity", tags = { "USER, ADMIN" })
    public Session getSession(@PathVariable(name = "sessionId") UUID sessionId) {
        return sessionService.getSession(sessionId);
    }

    @GetMapping(value = "/user/sessions")
    @Operation(summary = "Get Sessions", description = "Get all sessionEntities", tags = { "USER, ADMIN" })
    public ResponseEntity<List<Session>> getSessions() {
        return ResponseEntity.ok(sessionService.getSessions());
    }

    @PutMapping(value = "/admin/sessions/{sessionId}/status")
    @Operation(summary = "Update SessionEntity Status", description = "Update sessionEntity's status. valid values {INACTIVE, ONGOING, PAUSED, ENDED}", tags = { "ADMIN" })
    public ResponseEntity<ResponseMessage> changeSessionStatus(@PathVariable(name = "sessionId") UUID sessionId, @RequestBody RequestProps props ) {
        return new ResponseEntity<>(sessionService.changeStatus(sessionId, props.status()), HttpStatus.PARTIAL_CONTENT);
    }

    @PostMapping(value = "/admin/sessions/{sessionId}/assign/challenges")
    @Operation(summary = "Assign Challenges", description = "Assign challengeEntities to sessionEntity", tags = { "ADMIN" })
    public ResponseEntity<Session> assignChallenges(@PathVariable(name = "sessionId") UUID sessionId, @RequestBody RequestProps props) {
        return new ResponseEntity<>(sessionService.assignChallenges(sessionId, props.ids()), HttpStatus.PARTIAL_CONTENT);
    }
    @PostMapping(value = "/admin/sessions/{sessionId}/remove/challenges")
    @Operation(summary = "Remove Challenges", description = "Remove challengeEntities from sessionEntity", tags = { "ADMIN" })
    public ResponseEntity<Session> removeChallenges(@PathVariable(name = "sessionId") UUID sessionId, @RequestBody RequestProps props) {
        return new ResponseEntity<>(sessionService.removeChallenges(sessionId, props.ids()), HttpStatus.PARTIAL_CONTENT);
    }
    @PostMapping(value = "/admin/sessions/{sessionId}/assign/challenges/{challengeId}")
    @Operation(summary = "Assign ChallengeEntity", description = "Assign a challengeEntity to sessionEntity", tags = { "ADMIN" })
    public ResponseEntity<Session> assignChallenge(@PathVariable(name = "sessionId") UUID sessionId, @PathVariable(name = "challengeId") UUID challengeId) {
        return new ResponseEntity<>(sessionService.assignChallenge(sessionId, challengeId), HttpStatus.PARTIAL_CONTENT);
    }
    @PostMapping(value = "/admin/sessions/{sessionId}/remove/challenges/{challengeId}")
    @Operation(summary = "Remove A ChallengeEntity", description = "Remove a challengeEntity from sessionEntity", tags = { "ADMIN" })
    public ResponseEntity<Session> removeChallenge(@PathVariable(name = "sessionId") UUID sessionId, @PathVariable(name = "challengeId") UUID challengeId) {
        return new ResponseEntity<>(sessionService.removeChallenge(sessionId, challengeId), HttpStatus.PARTIAL_CONTENT);
    }

    @GetMapping(value = "/admin/users/{userId}/sessions")
    @Operation(summary = "Get Sessions", description = "Get all userEntity's sessionEntities", tags = { "ADMIN" })
    public ResponseEntity<List<Session>> getUserSessions(@PathVariable(name = "userId") UUID userId) {
        return ResponseEntity.ok(sessionService.getUserSessions(userId));
    }

}
