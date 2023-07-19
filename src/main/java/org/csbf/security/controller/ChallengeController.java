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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@SecurityRequirement(name = "ApiKey")
public class ChallengeController {

    private final ChallengeService challengeService;
    @PostMapping(value = "/secure/admin/store-ses")
    @Operation(summary = "Create Challenges", description = "Create new challenge", tags = { "admin" })
    protected ResponseEntity<ResponseMessage> createSession(@RequestBody HelperDto.ChallengeCreateDto challengeCreateDto) {
        return new ResponseEntity<>(challengeService.store(challengeCreateDto), HttpStatus.CREATED);
    }
}
