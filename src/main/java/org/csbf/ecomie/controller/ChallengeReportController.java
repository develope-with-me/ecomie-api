package org.csbf.ecomie.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csbf.ecomie.service.ChallengeReportService;
import org.csbf.ecomie.utils.helperclasses.HelperDomain.*;
import org.csbf.ecomie.utils.helperclasses.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/secure")
@SecurityRequirement(name = "ApiKey")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "ChallengeReportController", description = "This controller contains endpoints for reports")
public class ChallengeReportController {

private final ChallengeReportService reportService;
    private final ChallengeReportService challengeReportService;

    @PostMapping(value = "/ecomiest/reports/session/{sessionId}")
    @Operation(summary = "Create Challenge Report", description = "Create new report", tags = { "ECOMIEST" })
    protected ResponseEntity<ResponseMessage> createChallengeReport(@PathVariable(name = "sessionId") UUID sessionId, @RequestBody ChallengeReportRequest challengeReportRequest) {
        return new ResponseEntity<>(reportService.storeReport(sessionId, challengeReportRequest), HttpStatus.CREATED);
    }

    @PostMapping(value = "/admin/reports/user/{userId}/session/{sessionId}")
    @Operation(summary = "Create Challenge Report", description = "Create new report", tags = { "ADMIN" })
    protected ResponseEntity<ResponseMessage> createChallengeReportForUser(@PathVariable(name = "userId") UUID userId, @PathVariable(name = "sessionId") UUID sessionId, @RequestBody ChallengeReportRequest challengeReportRequest) {
        return new ResponseEntity<>(reportService.storeUserReport(userId, sessionId, challengeReportRequest), HttpStatus.CREATED);
    }

    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    @PutMapping(value = "/ecomiest/reports/{id}")
    @Operation(summary = "Update Challenge Report", description = "Update challenge report", tags = { "ECOMIEST" })
    public ResponseMessage updateChallengeReport(@PathVariable(name = "id") UUID id, @RequestBody ChallengeReportRequest challengeReportRequest) {
        return reportService.updateChallengeReport(id, challengeReportRequest);
    }

    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    @PutMapping(value = "/admin/reports/{id}")
    @Operation(summary = "Update Challenge Report", description = "Update challenge report", tags = { "ADMIN" })
    public ResponseMessage updateChallengeReportForUser(@PathVariable(name = "id") UUID id, @RequestBody ChallengeReportRequest challengeReportRequest) {
        return reportService.updateChallengeReportForUser(id, challengeReportRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/ecomiest/reports/{id}")
    @Operation(summary = "Get Challenge Report", description = "Get challenge report", tags = { "ECOMIEST", "ADMIN" })
    public ChallengeReport getChallengeReport(@PathVariable(name = "id") UUID id) {
        return reportService.getChallengeReport(id);
    }

    @GetMapping(value = "/ecomiest/reports")
    @Operation(summary = "Get Challenge Reports", description = "Get all challenge reports", tags = { "ECOMIEST", "ADMIN" })
    public ResponseEntity<List<ChallengeReport>> getChallengeReports(@RequestParam("sessionId") Optional<UUID> sessionId, @RequestParam("challengeId") Optional<UUID> challengeId) {
        return ResponseEntity.ok(reportService.getChallengeReports(sessionId, challengeId));
    }

//    @GetMapping(value = "/admin/reports/challenge/{challengeId}")
//    @Operation(summary = "Get Challenge's Reports", description = "Get a challenge's reports", tags = { "ADMIN" })
//    public ResponseEntity<List<ChallengeReport>> getChallengeReports() {
//        return ResponseEntity.ok(reportService.getChallengeReports(challengeId));
//    }

    @DeleteMapping(value = "/ecomiest/reports/{id}")
    @Operation(summary = "Delete Report", description = "Delete Challenge", tags = { "ECOMIEST", "ADMIN" })
    public ResponseEntity<ResponseMessage> deleteChallengeReport(@PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(reportService.deleteChallengeReport(id));
    }
}
