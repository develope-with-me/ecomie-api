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
    public ResponseMessage storeReport(UUID sessionId, ChallengeReportRequest challengeReportRequest) {
//        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();

        var user = userRepo.findByEmail(authContext.getAuthUser().getName()).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("UserEntity", "User with email (%s) not found".formatted(authContext.getAuthUser().getName())).toException());
        return store(sessionId, challengeReportRequest, user);
    }

    @Override
    public ResponseMessage storeUserReport(UUID userId, UUID sessionId, ChallengeReportRequest challengeReportRequest) {
        var user = userRepo.findById(userId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("UserEntity", "User with id (%s) not found".formatted(userId.toString())).toException());
        return store(sessionId, challengeReportRequest, user);
    }

    @NotNull
    private ResponseMessage store(UUID sessionId, ChallengeReportRequest challengeReportRequest, UserEntity userEntity) {
        var session = sessionRepo.findById(sessionId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("SessionEntity", "Session with id (%s) not found".formatted(sessionId.toString())).toException());
        if(!session.getStatus().equals(SessionStatus.ONGOING)) {
            throw Problems.BAD_REQUEST.withProblemError("sessionId", "Session (%s) is not ongoing".formatted(sessionId)).toException();
        }
        var subscription = subscriptionRepo.findBySessionAndUser(session, userEntity).orElseThrow(() -> Problems.NOT_FOUND.withDetail("Subscription not found").toException());

        var report = ChallengeReportEntity.builder()
                .subscription(subscription)
                .user(userEntity)
                .numberEvangelizedTo(challengeReportRequest.numberEvangelizedTo())
                .numberFollowedUp(challengeReportRequest.numberFollowedUp())
                .numberOfNewConverts(challengeReportRequest.numberOfNewConverts())
                .difficulties(challengeReportRequest.difficulties())
                .remark(challengeReportRequest.remark())
                .build();

        reportRepo.save(report);

        return new ResponseMessage.SuccessResponseMessage("Created report successfully");
    }

    @Override
    public ResponseMessage updateChallengeReport(UUID reportId, ChallengeReportRequest challengeReportRequest) {
//        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        var report = reportRepo.findById(reportId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("ReportEntity", "Report with id (%s) not found".formatted(reportId.toString())).toException());
        if(!report.getSubscription().getUser().getEmail().equals(authContext.getAuthUser().getName())) {
            throw Problems.BAD_REQUEST.withDetail("Report is not yours").toException();
        }

        return update(challengeReportRequest, report);
    }

    @NotNull
    private ResponseMessage update(ChallengeReportRequest challengeReportRequest, ChallengeReportEntity report) {
        report.setNumberEvangelizedTo(challengeReportRequest.numberEvangelizedTo());
        report.setNumberFollowedUp(challengeReportRequest.numberFollowedUp());
        report.setNumberOfNewConverts(challengeReportRequest.numberOfNewConverts());
        report.setDifficulties(challengeReportRequest.difficulties());
        report.setRemark(challengeReportRequest.remark());

        reportRepo.save(report);
        return new ResponseMessage.SuccessResponseMessage("Updated report successfully");
    }

    @Override
    public ResponseMessage updateChallengeReportForUser(UUID reportId, ChallengeReportRequest challengeReportRequest) {
        var report = reportRepo.findById(reportId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("ReportEntity", "Report with id (%s) not found".formatted(reportId.toString())).toException());

        return update(challengeReportRequest, report);

    }

    @Override
    public ChallengeReport getChallengeReport(UUID reportId) {
//        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        ChallengeReportEntity report = null;
        if (authContext.isAuthorized(Role.ADMIN)) {
            report = reportRepo.findById(reportId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("ReportEntity", "Report with id (%s) not found".formatted(reportId.toString())).toException());
        } else {
            report = reportRepo.findByIdAndUser_Email(reportId, authContext.getAuthUser().getName()).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("ReportEntity", "User not subscribed".formatted(reportId.toString())).toException());
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
    public ResponseMessage deleteChallengeReport(UUID id) {
        var report = reportRepo.findById(id).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("ReportEntity", "Report with id (%s) not found".formatted(id.toString())).toException());
        if(!authContext.isAuthorized(Role.ADMIN) && !report.getSubscription().getUser().getEmail().equals(authContext.getAuthUser().getName())) {
            throw Problems.UNAUTHORIZED.withDetail("Report is not yours").toException();
        }
        reportRepo.deleteById(id);
        return new ResponseMessage.SuccessResponseMessage("Report deleted successfully");
    }
}
