package org.csbf.security.service;

import org.csbf.security.utils.helperclasses.HelperDomain;
import org.csbf.security.utils.helperclasses.ResponseMessage;

import java.util.List;
import java.util.UUID;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
public interface ChallengeReportService {
    ResponseMessage storeReport(UUID sessionId, HelperDomain.ChallengeReportCreateDto challengeReportCreateDto);

    ResponseMessage storeUserReport(UUID userId, UUID sessionId, HelperDomain.ChallengeReportCreateDto challengeReportCreateDto);

    ResponseMessage updateChallengeReport(UUID reportId, HelperDomain.ChallengeReportCreateDto challengeReportCreateDto);

    ResponseMessage updateChallengeReportForUser(UUID reportId, HelperDomain.ChallengeReportCreateDto challengeReportCreateDto);

    HelperDomain.ChallengeReportFullDto getChallengeReport(UUID reportId);

    List<HelperDomain.ChallengeReportFullDto> getChallengeReports();
}

