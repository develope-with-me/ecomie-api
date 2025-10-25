package org.csbf.ecomie.service.imp;

import lombok.RequiredArgsConstructor;
import org.csbf.ecomie.config.AuthContext;
import org.csbf.ecomie.constant.Role;
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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Service
@RequiredArgsConstructor
public class ChallengeReportServiceImp implements ChallengeReportService {
    private final ChallengeReportRepository reportRepo;
    private final SessionRepository sessionRepo;
    private final SubscriptionRepository subscriptionRepo;
    private final UserRepository userRepo;
    private final AuthContext authContext;
    private final ChallengeReportMapper mapper;


    @Override
    public ResponseMessage storeReport(UUID sessionId, ChallengeReportRequest challengeReportRequest) {
//        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();

        var user = userRepo.findByEmail(authContext.getAuthUser().getName()).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "UserEntity with email (%s) not found".formatted(authContext.getAuthUser().getName())).toException());
        return store(sessionId, challengeReportRequest, user);
    }

    @Override
    public ResponseMessage storeUserReport(UUID userId, UUID sessionId, ChallengeReportRequest challengeReportRequest) {
        var user = userRepo.findById(userId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "UserEntity with id (%s) not found".formatted(userId.toString())).toException());
        return store(sessionId, challengeReportRequest, user);
    }

    @NotNull
    private ResponseMessage store(UUID sessionId, ChallengeReportRequest challengeReportRequest, UserEntity userEntity) {
        var session = sessionRepo.findById(sessionId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "SessionEntity with id (%s) not found".formatted(sessionId.toString())).toException());

        var subscription = subscriptionRepo.findBySessionAndUser(session, userEntity).orElseThrow(() -> Problems.NOT_FOUND.withDetail("SubscriptionEntity not found").toException());

        var report = ChallengeReportEntity.builder()
                .subscription(subscription)
                .numberEvangelizedTo(challengeReportRequest.numberEvangelizedTo())
                .numberFollowedUp(challengeReportRequest.numberFollowedUp())
                .numberOfNewConverts(challengeReportRequest.numberOfNewConverts())
                .difficulties(challengeReportRequest.difficulties())
                .remark(challengeReportRequest.remark())
                .build();

        reportRepo.save(report);

        return new ResponseMessage.SuccessResponseMessage("Updated report successfully");
    }

    @Override
    public ResponseMessage updateChallengeReport(UUID reportId, ChallengeReportRequest challengeReportRequest) {
//        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        var report = reportRepo.findById(reportId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("report", "Report with id (%s) not found".formatted(reportId.toString())).toException());
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
        var report = reportRepo.findById(reportId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("report", "Report with id (%s) not found".formatted(reportId.toString())).toException());

        return update(challengeReportRequest, report);

    }

    @Override
    public ChallengeReport getChallengeReport(UUID reportId) {
//        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        var report = reportRepo.findById(reportId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("report", "Report with id (%s) not found".formatted(reportId.toString())).toException());
//        if (!authUser.getAuthorities().contains("ADMIN")) {
//        if (authContext.getAuthUser().getAuthorities().stream().noneMatch(authority -> authority.getAuthority().equals(Role.ADMIN.name()))) {
        if (!authContext.isAuthorized(Role.ADMIN)) {            
            if (!report.getSubscription().getUser().getEmail().equals(authContext.getAuthUser().getName())) {
                throw Problems.BAD_REQUEST.withDetail("UserEntity not subscribed").toException();
            }

            return mapper.asDomainObject(report).justMinimal();
        }

        return mapper.asDomainObject(report);
    }



    @Override
    public List<ChallengeReport> getChallengeReports() {
//        List<ChallengeReport> challengeReports = new ArrayList<>();
//        reportRepo.findAll().forEach(report -> challengeReports.add(new ChallengeReport(report)));
        var reportEntities = reportRepo.findAll();
        var reports = mapper.asDomainObjects(reportEntities);

        return reports.stream().map(ChallengeReport::justMinimal).collect(Collectors.toList());
    }
}
