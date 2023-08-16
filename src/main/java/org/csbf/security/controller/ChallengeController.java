package org.csbf.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csbf.security.service.ChallengeService;
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
public class ChallengeController {

    private final ChallengeService challengeService;
    @PostMapping(value = "/secure/admin/store-challenge")
    @Operation(summary = "Create Challenges", description = "Create new challenge", tags = { "admin" })
    protected ResponseEntity<ResponseMessage> createChallenge(@RequestBody HelperDto.ChallengeCreateDto challengeCreateDto) {
        return new ResponseEntity<>(challengeService.store(challengeCreateDto), HttpStatus.CREATED);
    }

    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    @PostMapping(value = "/secure/admin/update-challenge/{challengeId}")
    @Operation(summary = "Update Challenge", description = "Update challenge", tags = { "admin" })
    public ResponseMessage updateChallenge(@PathVariable(name = "challengeId") UUID challengeId, @RequestBody HelperDto.ChallengeCreateDto challengeCreateDto) {
        return challengeService.update(challengeId, challengeCreateDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/secure/user/challenge/{challengeId}")
    @Operation(summary = "Get Challenge", description = "Get challenge", tags = { "user", "admin" })
    public HelperDto.ChallengeFullDto getChallenge(@PathVariable(name = "challengeId") UUID challengeId) {
        return challengeService.getChallenge(challengeId);
    }

    @GetMapping(value = "/secure/admin/get-challenge")
    @Operation(summary = "Get Challenges", description = "Get all challenges", tags = { "admin" })
    public ResponseEntity<List<HelperDto.ChallengeFullDto>> getChallenges() {
        return ResponseEntity.ok(challengeService.getChallenges());
    }

    @PostMapping(value = "/secure/admin/challenge-status/{challengeId}")
    @Operation(summary = "Update Challenge Status", description = "Update challenge's status. valid values {INACTIVE, ONGOING, PAUSED, ENDED}", tags = { "user", "admin" })
    public ResponseEntity<ResponseMessage> changeChallengeStatus(@PathVariable(name = "challengeId") UUID challengeId, @RequestParam String status) {
        return new ResponseEntity<>(challengeService.changeType(challengeId, status), HttpStatus.PARTIAL_CONTENT);
    }
}
