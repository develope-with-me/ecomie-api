package org.csbf.ecomie.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csbf.ecomie.service.ChallengeService;
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
@Tag(name = "ChallengeController", description = "This controller contains endpoints for challenges")
public class ChallengeController {

    private final ChallengeService challengeService;
    @PostMapping(value = "/secure/admin/challenges")
    @Operation(summary = "Create Challenges", description = "Create new challenge", tags = { "ADMIN" })
    protected ResponseEntity<ResponseMessage> createChallenge(@RequestBody Challenge challenge) {
        return new ResponseEntity<>(challengeService.store(challenge), HttpStatus.CREATED);
    }

    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    @PutMapping(value = "/secure/admin/challenges/{id}")
    @Operation(summary = "Update Challenge", description = "Update challenge", tags = { "USER, ADMIN" })
    public ResponseMessage updateChallenge(@PathVariable(name = "id") UUID id, @RequestBody Challenge challenge) {
        return challengeService.update(id, challenge);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/secure/user/challenges/{id}")
    @Operation(summary = "Get Challenge", description = "Get challenge", tags = { "USER, ADMIN" })
    public Challenge getChallenge(@PathVariable(name = "id") UUID id) {
        return challengeService.getChallenge(id);
    }

   @GetMapping(value = "/challenges")
    @Operation(summary = "Get Challenges", description = "Get all challenges", tags = { "UNAUTHENTICATED" })
    public ResponseEntity<List<Challenge>> getChallenges() {
        return ResponseEntity.ok(challengeService.getChallenges());
    }


    @PutMapping(value = "/secure/admin/challenges/{id}/type")
    @Operation(summary = "Update Challenge Type", description = "Update challenge's Type. valid values {NORMAL, EVENT}", tags = { "ADMIN" })
    public ResponseEntity<ResponseMessage> changeChallengeStatus(@PathVariable(name = "id") UUID id, @RequestBody RequestProps props) {
        return new ResponseEntity<>(challengeService.changeType(id, props.type()), HttpStatus.PARTIAL_CONTENT);
    }

    @DeleteMapping(value = "/secure/admin/challenges/{id}")
    @Operation(summary = "Delete Challenge", description = "Delete Challenge", tags = { "ADMIN" })
    public ResponseEntity<ResponseMessage> deleteChallenge(@PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(challengeService.deleteChallenge(id));
    }
}
