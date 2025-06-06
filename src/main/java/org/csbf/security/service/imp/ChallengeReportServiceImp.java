package org.csbf.security.service.imp;

import lombok.RequiredArgsConstructor;
import org.csbf.security.config.AuthContext;
import org.csbf.security.constant.Role;
import org.csbf.security.exceptions.BadRequestException;
import org.csbf.security.exceptions.ResourceNotFoundException;
import org.csbf.security.model.ChallengeReport;
import org.csbf.security.model.User;
import org.csbf.security.repository.ChallengeReportRepository;
import org.csbf.security.repository.SessionRepository;
import org.csbf.security.repository.SubscriptionRepository;
import org.csbf.security.repository.UserRepository;
import org.csbf.security.service.ChallengeReportService;
import org.csbf.security.utils.helperclasses.HelperDto;
import org.csbf.security.utils.helperclasses.ResponseMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChallengeReportServiceImp implements ChallengeReportService {
    private final ChallengeReportRepository reportRepo;
    private final SessionRepository sessionRepo;
    private final SubscriptionRepository subscriptionRepo;
    private final UserRepository userRepo;
    private final AuthContext authContext;

    @Override
    public ResponseMessage storeReport(UUID sessionId, HelperDto.ChallengeReportCreateDto challengeReportCreateDto) {
//        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();

        var user = userRepo.findByEmail(authContext.getAuthUser().getName()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return store(sessionId, challengeReportCreateDto, user);
    }

    @Override
    public ResponseMessage storeUserReport(UUID userId, UUID sessionId, HelperDto.ChallengeReportCreateDto challengeReportCreateDto) {
        var user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return store(sessionId, challengeReportCreateDto, user);
    }

    @NotNull
    private ResponseMessage store(UUID sessionId, HelperDto.ChallengeReportCreateDto challengeReportCreateDto, User user) {
        var session = sessionRepo.findById(sessionId).orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        var subscription = subscriptionRepo.findBySessionAndUser(session, user).orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        var report = ChallengeReport.builder()
                .subscription(subscription)
                .numberEvangelizedTo(challengeReportCreateDto.numberEvangelizedTo())
                .numberFollowedUp(challengeReportCreateDto.numberFollowedUp())
                .numberOfNewConverts(challengeReportCreateDto.numberOfNewConverts())
                .difficulties(challengeReportCreateDto.difficulties())
                .remark(challengeReportCreateDto.remark())
                .build();

        reportRepo.save(report);

        return new ResponseMessage.SuccessResponseMessage("Updated report successfully");
    }

    @Override
    public ResponseMessage updateChallengeReport(UUID reportId, HelperDto.ChallengeReportCreateDto challengeReportCreateDto) {
//        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        var report = reportRepo.findById(reportId).orElseThrow(() -> new ResourceNotFoundException("Report does not exist"));
        if(!report.getSubscription().getUser().getEmail().equals(authContext.getAuthUser().getName())) {
            throw new BadRequestException.InvalidAuthenticationRequestException("Report is not yours");
        }

        return update(challengeReportCreateDto, report);
    }

    @NotNull
    private ResponseMessage update(HelperDto.ChallengeReportCreateDto challengeReportCreateDto, ChallengeReport report) {
        report.setNumberEvangelizedTo(challengeReportCreateDto.numberEvangelizedTo());
        report.setNumberFollowedUp(challengeReportCreateDto.numberFollowedUp());
        report.setNumberOfNewConverts(challengeReportCreateDto.numberOfNewConverts());
        report.setDifficulties(challengeReportCreateDto.difficulties());
        report.setRemark(challengeReportCreateDto.remark());

        reportRepo.save(report);
        return new ResponseMessage.SuccessResponseMessage("Updated report successfully");
    }

    @Override
    public ResponseMessage updateChallengeReportForUser(UUID reportId, HelperDto.ChallengeReportCreateDto challengeReportCreateDto) {
        var report = reportRepo.findById(reportId).orElseThrow(() -> new ResourceNotFoundException("Report does not exist"));

        return update(challengeReportCreateDto, report);

    }

    @Override
    public HelperDto.ChallengeReportFullDto getChallengeReport(UUID reportId) {
//        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        var report = reportRepo.findById(reportId).orElseThrow(() -> new ResourceNotFoundException("Report does not exist"));
//        if (!authUser.getAuthorities().contains("ADMIN")) {
        if (authContext.getAuthUser().getAuthorities().stream().noneMatch(authority -> authority.getAuthority().equals(Role.ADMIN.name()))) {
            if (!report.getSubscription().getUser().getEmail().equals(authContext.getAuthUser().getName())) {
                throw new BadRequestException.InvalidAuthenticationRequestException("User not subscribed");
            }
        }
        return new HelperDto.ChallengeReportFullDto(report);
    }



    @Override
    public List<HelperDto.ChallengeReportFullDto> getChallengeReports() {
        List<HelperDto.ChallengeReportFullDto> challengeReports = new ArrayList<>();
        reportRepo.findAll().forEach(report -> challengeReports.add(new HelperDto.ChallengeReportFullDto(report)));
        return challengeReports;
    }
}
