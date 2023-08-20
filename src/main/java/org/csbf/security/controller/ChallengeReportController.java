package org.csbf.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csbf.security.service.ChallengeReportService;
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
public class ChallengeReportController {

private final ChallengeReportService reportService;
    @PostMapping(value = "/secure/user/store-report/session/{sessionId}")
    @Operation(summary = "Create Challenge Report", description = "Create new report", tags = { "USER", "ChallengeReportController" })
    protected ResponseEntity<ResponseMessage> createChallengeReport(@PathVariable(name = "sessionId") UUID sessionId, @RequestBody HelperDto.ChallengeReportCreateDto challengeReportCreateDto) {
        return new ResponseEntity<>(reportService.storeReport(sessionId, challengeReportCreateDto), HttpStatus.CREATED);
    }

    @PostMapping(value = "/secure/admin/store-report/user/{userId}/session/{sessionId}")
    @Operation(summary = "Create Challenge Report", description = "Create new report", tags = { "ADMIN", "ChallengeReportController" })
    protected ResponseEntity<ResponseMessage> createChallengeReportForUser(@PathVariable(name = "userId") UUID userId, @PathVariable(name = "sessionId") UUID sessionId, @RequestBody HelperDto.ChallengeReportCreateDto challengeReportCreateDto) {
        return new ResponseEntity<>(reportService.storeUserReport(userId, sessionId, challengeReportCreateDto), HttpStatus.CREATED);
    }

    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    @PostMapping(value = "/secure/user/update-report/{reportId}")
    @Operation(summary = "Update Challenge Report", description = "Update challenge report", tags = { "USER", "ChallengeReportController" })
    public ResponseMessage updateChallengeReport(@PathVariable(name = "reportId") UUID reportId, @RequestBody HelperDto.ChallengeReportCreateDto challengeReportCreateDto) {
        return reportService.updateChallengeReport(reportId, challengeReportCreateDto);
    }

    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    @PostMapping(value = "/secure/admin/update-report/{reportId}/user/{userId}")
    @Operation(summary = "Update Challenge Report", description = "Update challenge report", tags = { "ADMIN", "ChallengeReportController" })
    public ResponseMessage updateChallengeReportForUser(@PathVariable(name = "reportId") UUID reportId, @RequestBody HelperDto.ChallengeReportCreateDto challengeReportCreateDto) {
        return reportService.updateChallengeReportForUser(reportId, challengeReportCreateDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/secure/user/report/{reportId}")
    @Operation(summary = "Get Challenge Report", description = "Get challenge report", tags = { "USER", "ChallengeReportController" })
    public HelperDto.ChallengeReportFullDto getChallengeReport(@PathVariable(name = "reportId") UUID reportId) {
        return reportService.getChallengeReport(reportId);
    }

    @GetMapping(value = "/secure/admin/get-reports")
    @Operation(summary = "Get Challenge Reports", description = "Get all challenge reports", tags = { "ADMIN", "ChallengeReportController" })
    public ResponseEntity<List<HelperDto.ChallengeReportFullDto>> getChallengeReports() {
        return ResponseEntity.ok(reportService.getChallengeReports());
    }
}
