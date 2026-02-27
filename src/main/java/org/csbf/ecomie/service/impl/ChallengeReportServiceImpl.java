package org.csbf.ecomie.service.impl;

import lombok.RequiredArgsConstructor;
import org.csbf.ecomie.config.AuthContext;
import org.csbf.ecomie.constant.Role;
import org.csbf.ecomie.constant.SessionStatus;
import org.csbf.ecomie.exceptions.Problems;
import org.csbf.ecomie.entity.ChallengeReportEntity;
import org.csbf.ecomie.entity.UserEntity;
import org.csbf.ecomie.mapper.ChallengeReportMapper;
import org.csbf.ecomie.repository.ChallengeReportRepository;
import org.csbf.ecomie.repository.SessionRepository;
import org.csbf.ecomie.repository.SubscriptionRepository;
import org.csbf.ecomie.repository.UserRepository;
import org.csbf.ecomie.service.ChallengeReportService;
import org.csbf.ecomie.utils.helperclasses.HelperDomain.*;
import org.csbf.ecomie.utils.helperclasses.ResponseMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Service
@RequiredArgsConstructor
public class ChallengeReportServiceImpl implements ChallengeReportService {
    private final ChallengeReportRepository reportRepo;
    private final SessionRepository sessionRepo;
    private final SubscriptionRepository subscriptionRepo;
    private final UserRepository userRepo;
    private final AuthContext authContext;
    private final ChallengeReportMapper mapper;


    @Override
    public ResponseMessage<ChallengeReport> storeReport(UUID subscriptionId, ChallengeReportRequest challengeReportRequest) {
//        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();

        var user = userRepo.findByEmail(authContext.getAuthUser().getName()).orElseThrow(
                () -> Problems.NOT_FOUND.withProblemError("UserEntity",
                        "User with email (%s) not found".formatted(authContext.getAuthUser().getName())).toException());
        return store(subscriptionId, challengeReportRequest, user);
    }

    @Override
    public ResponseMessage<ChallengeReport> storeUserReport(UUID userId, UUID subscriptionId, ChallengeReportRequest challengeReportRequest) {
        var user = userRepo.findById(userId).orElseThrow(
                () -> Problems.NOT_FOUND.withProblemError("UserEntity",
                        "User with id (%s) not found".formatted(userId.toString())).toException());
        return store(subscriptionId, challengeReportRequest, user);
    }

    @NotNull
    private ResponseMessage<ChallengeReport> store(UUID subscriptionId, ChallengeReportRequest challengeReportRequest, UserEntity userEntity) {
        var subscription = subscriptionRepo.findById(subscriptionId).orElseThrow(
                () -> Problems.NOT_FOUND.withProblemError("subscriptionEntity",
                        "Subscription with id (%s) not found".formatted(subscriptionId.toString())).toException());

        var report = ChallengeReportEntity.builder()
                .subscription(subscription)
                .user(userEntity)
                .numberEvangelizedTo(challengeReportRequest.numberEvangelizedTo())
                .numberFollowedUp(challengeReportRequest.numberFollowedUp())
                .numberOfNewConverts(challengeReportRequest.numberOfNewConverts())
                .difficulties(challengeReportRequest.difficulties())
                .remark(challengeReportRequest.remark())
                .build();

        ChallengeReportEntity savedReport = reportRepo.save(report);

        return new ResponseMessage.SuccessResponseMessage<>("Created report successfully",
                mapper.asDomainObject(savedReport).justMinimal());
    }

    @Override
    public ResponseMessage<ChallengeReport> updateChallengeReport(UUID reportId, ChallengeReportRequest challengeReportRequest) {
//        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        var report = reportRepo.findById(reportId).orElseThrow(
                () -> Problems.NOT_FOUND.withProblemError("ReportEntity",
                        "Report with id (%s) not found".formatted(reportId.toString())).toException());
        if(!report.getSubscription().getUser().getEmail().equals(authContext.getAuthUser().getName())) {
            throw Problems.BAD_REQUEST.withDetail("Report is not yours").toException();
        }

        return update(challengeReportRequest, report);
    }

    @NotNull
    private ResponseMessage<ChallengeReport> update(ChallengeReportRequest challengeReportRequest, ChallengeReportEntity report) {
        report.setNumberEvangelizedTo(challengeReportRequest.numberEvangelizedTo());
        report.setNumberFollowedUp(challengeReportRequest.numberFollowedUp());
        report.setNumberOfNewConverts(challengeReportRequest.numberOfNewConverts());
        report.setDifficulties(challengeReportRequest.difficulties());
        report.setRemark(challengeReportRequest.remark());

        ChallengeReportEntity updatedReport = reportRepo.save(report);
        return new ResponseMessage.SuccessResponseMessage<>("Updated report successfully",
                mapper.asDomainObject(updatedReport).justMinimal());
    }

    @Override
    public ResponseMessage<ChallengeReport> updateChallengeReportForUser(UUID reportId, ChallengeReportRequest challengeReportRequest) {
        var report = reportRepo.findById(reportId).orElseThrow(
                () -> Problems.NOT_FOUND.withProblemError("ReportEntity",
                        "Report with id (%s) not found".formatted(reportId.toString())).toException());

        return update(challengeReportRequest, report);

    }

    @Override
    public ChallengeReport getChallengeReport(UUID reportId) {
//        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        ChallengeReportEntity report = null;
        if (authContext.isAuthorized(Role.ADMIN)) {
            report = reportRepo.findById(reportId).orElseThrow(
                    () -> Problems.NOT_FOUND.withProblemError("ReportEntity",
                            "Report with id (%s) not found".formatted(reportId.toString())).toException());
        } else {
            report = reportRepo.findByIdAndUser_Email(reportId, authContext.getAuthUser().getName())
                    .orElseThrow(() -> Problems.NOT_FOUND.withProblemError("ReportEntity",
                            "User not subscribed").toException());
        }
        return mapper.asDomainObject(report).justMinimal();
    }



    @Override
    public List<ChallengeReport> getChallengeReports(Optional<UUID> sessionId, Optional<UUID> challengeId) {
        List<ChallengeReportEntity> reportEntities = new ArrayList<>();

        if (authContext.isAuthorized(Role.ADMIN)) {
            if(sessionId.isPresent() && challengeId.isPresent()) {
                reportEntities =reportRepo.findAllBySubscription_Session_IdAndSubscription_Challenge_Id(sessionId.get(), challengeId.get());
            } else if(sessionId.isPresent()) {
                reportEntities = reportRepo.findAllBySubscription_Session_Id(sessionId.get());
            } else if(challengeId.isPresent()) {
                reportEntities = reportRepo.findAllBySubscription_Challenge_Id(challengeId.get());
            } else {
                reportEntities = reportRepo.findAll();
            }
        } else {
            String email = authContext.getAuthUser().getName();
            if(sessionId.isPresent() && challengeId.isPresent()) {
                reportEntities =reportRepo.findAllByUser_EmailAndSubscription_Session_IdAndSubscription_Challenge_Id(email, sessionId.get(), challengeId.get());
            } else if(sessionId.isPresent()) {
                reportEntities = reportRepo.findAllByUser_EmailAndSubscription_Session_Id(email, sessionId.get());
            } else if(challengeId.isPresent()) {
                reportEntities = reportRepo.findAllByUser_EmailAndSubscription_Challenge_Id(email, challengeId.get());
            } else {
                reportEntities = reportRepo.findAllByUser_Email(email);
            }
        }

        var reports = mapper.asDomainObjects(reportEntities);
        return reports.stream().map(ChallengeReport::justMinimal).collect(Collectors.toList());
    }

    @Override
    public List<ChallengeReport> getChallengeReportsOfAChallenge(UUID challengeId) {
        var reportEntities = reportRepo.findAllBySubscription_Challenge_Id(challengeId);
        var reports =  mapper.asDomainObjects(reportEntities);
        return reports.stream().map(ChallengeReport::justMinimal).collect(Collectors.toList());
    }

    @Override
    public ResponseMessage<ChallengeReport> deleteChallengeReport(UUID id) {
        var report = reportRepo.findById(id).orElseThrow(
                () -> Problems.NOT_FOUND.withProblemError("ReportEntity",
                        "Report with id (%s) not found".formatted(id.toString())).toException());
        if(!authContext.isAuthorized(Role.ADMIN)
                && !report.getSubscription().getUser().getEmail().equals(authContext.getAuthUser().getName())) {
            throw Problems.UNAUTHORIZED.withDetail("Report is not yours").toException();
        }
        reportRepo.deleteById(id);
        return new ResponseMessage.SuccessResponseMessage<>("Report deleted successfully");
    }
}
