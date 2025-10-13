package org.csbf.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csbf.security.service.ChallengeService;
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
@Tag(name = "ChallengeController", description = "This controller contains endpoints for challengeEntities")
public class ChallengeController {

    private final ChallengeService challengeService;
    @PostMapping(value = "/admin/challenges")
    @Operation(summary = "Create Challenges", description = "Create new challengeEntity", tags = { "ADMIN" })
    protected ResponseEntity<ResponseMessage> createChallenge(@RequestBody Challenge challenge) {
        return new ResponseEntity<>(challengeService.store(challenge), HttpStatus.CREATED);
    }

    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    @PostMapping(value = "/admin/challenges/{challengeId}")
    @Operation(summary = "Update ChallengeEntity", description = "Update challengeEntity", tags = { "USER, ADMIN" })
    public ResponseMessage updateChallenge(@PathVariable(name = "challengeId") UUID challengeId, @RequestBody Challenge challenge) {
        return challengeService.update(challengeId, challenge);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/user/challenges/{challengeId}")
    @Operation(summary = "Get ChallengeEntity", description = "Get challengeEntity", tags = { "USER, ADMIN" })
    public Challenge getChallenge(@PathVariable(name = "challengeId") UUID challengeId) {
        return challengeService.getChallenge(challengeId);
    }

   @GetMapping(value = "/user/challenges")
    @Operation(summary = "Get Challenges", description = "Get all challengeEntities", tags = { "USER, ADMIN" })
    public ResponseEntity<List<Challenge>> getChallenges() {
        return ResponseEntity.ok(challengeService.getChallenges());
    }


    @PutMapping(value = "/admin/challenges/{challengeId}/type")
    @Operation(summary = "Update ChallengeEntity Type", description = "Update challengeEntity's Type. valid values {NORMAL, EVENT}", tags = { "ADMIN" })
    public ResponseEntity<ResponseMessage> changeChallengeStatus(@PathVariable(name = "challengeId") UUID challengeId, @RequestBody RequestProps props) {
        return new ResponseEntity<>(challengeService.changeType(challengeId, props.type()), HttpStatus.PARTIAL_CONTENT);
    }
}
