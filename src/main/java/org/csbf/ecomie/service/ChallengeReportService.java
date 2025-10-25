package org.csbf.ecomie.service;

import org.csbf.ecomie.utils.helperclasses.HelperDomain.*;
import org.csbf.ecomie.utils.helperclasses.ResponseMessage;

import java.util.List;
import java.util.UUID;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
public interface ChallengeReportService {
    ResponseMessage storeReport(UUID sessionId, ChallengeReportRequest challengeReportRequest);

    ResponseMessage storeUserReport(UUID userId, UUID sessionId, ChallengeReportRequest challengeReportRequest);

    ResponseMessage updateChallengeReport(UUID reportId, ChallengeReportRequest challengeReportRequest);

    ResponseMessage updateChallengeReportForUser(UUID reportId, ChallengeReportRequest challengeReportRequest);

    ChallengeReport getChallengeReport(UUID reportId);

    List<ChallengeReport> getChallengeReports();
}

