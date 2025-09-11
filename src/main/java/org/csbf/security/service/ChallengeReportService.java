package org.csbf.security.service;

import org.csbf.security.utils.helperclasses.HelperDto;
import org.csbf.security.utils.helperclasses.ResponseMessage;

import java.util.List;
import java.util.UUID;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
public interface ChallengeReportService {
    ResponseMessage storeReport(UUID sessionId, HelperDto.ChallengeReportCreateDto challengeReportCreateDto);

    ResponseMessage storeUserReport(UUID userId, UUID sessionId, HelperDto.ChallengeReportCreateDto challengeReportCreateDto);

    ResponseMessage updateChallengeReport(UUID reportId, HelperDto.ChallengeReportCreateDto challengeReportCreateDto);

    ResponseMessage updateChallengeReportForUser(UUID reportId, HelperDto.ChallengeReportCreateDto challengeReportCreateDto);

    HelperDto.ChallengeReportFullDto getChallengeReport(UUID reportId);

    List<HelperDto.ChallengeReportFullDto> getChallengeReports();
}

