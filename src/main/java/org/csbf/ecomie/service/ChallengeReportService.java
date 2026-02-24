package org.csbf.ecomie.service;

import org.csbf.ecomie.utils.helperclasses.HelperDomain.*;
import org.csbf.ecomie.utils.helperclasses.ResponseMessage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
public interface ChallengeReportService {
    ResponseMessage<ChallengeReport> storeReport(UUID sessionId, ChallengeReportRequest challengeReportRequest);

    ResponseMessage<ChallengeReport> storeUserReport(UUID userId, UUID sessionId, ChallengeReportRequest challengeReportRequest);

    ResponseMessage<ChallengeReport> updateChallengeReport(UUID reportId, ChallengeReportRequest challengeReportRequest);

    ResponseMessage<ChallengeReport> updateChallengeReportForUser(UUID reportId, ChallengeReportRequest challengeReportRequest);

    ChallengeReport getChallengeReport(UUID reportId);

    List<ChallengeReport> getChallengeReports(Optional<UUID> sessionId, Optional<UUID> challengeId);

    List<ChallengeReport> getChallengeReportsOfAChallenge(UUID challengeId);

    ResponseMessage<ChallengeReport> deleteChallengeReport(UUID id);
}

