package org.csbf.security.service.imp;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.csbf.security.constant.ChallengeType;
import org.csbf.security.exceptions.BadRequestException;
import org.csbf.security.exceptions.ResourceExistsException;
import org.csbf.security.exceptions.ResourceNotFoundException;
import org.csbf.security.model.Challenge;
import org.csbf.security.repository.ChallengeRepository;
import org.csbf.security.repository.SessionRepository;
import org.csbf.security.service.ChallengeService;
import org.csbf.security.utils.helperclasses.HelperDto;
import org.csbf.security.utils.helperclasses.ResponseMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChallengeServiceImp implements ChallengeService {

    private final ChallengeRepository challengeRepo;
    private final SessionRepository sessionRepo;

    @Override
    @Transactional
    public ResponseMessage store(HelperDto.ChallengeCreateDto challengeCreateDto) {

        if (!EnumUtils.isValidEnum(ChallengeType.class, challengeCreateDto.type().toUpperCase()))
            throw new BadRequestException("Invalid challenge type");
        challengeRepo.findByName(challengeCreateDto.name()).ifPresent(chal -> {throw new ResourceExistsException("challenge with the name '" + chal.getName() + "' already exist");});
        var challenge = Challenge.builder()
                .name(challengeCreateDto.name())
                .description(challengeCreateDto.description())
                .type(challengeCreateDto.type())
                .target(challengeCreateDto.target())
                .build();

        if(challengeCreateDto.sessionIds().length > 0) {
            Arrays.asList(challengeCreateDto.sessionIds()).forEach(id -> {
                var session = sessionRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("session not found"));
                challenge.addSession(session);

                sessionRepo.save(session);
            });

        }


        challengeRepo.save(challenge);
        return new ResponseMessage.SuccessResponseMessage("challenge created. Type: " + challenge.getType());

    }

    @Override
    public ResponseMessage changeType(UUID id, String status) {
        var challenge = challengeRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("challenge not found"));
        if (!EnumUtils.isValidEnum(ChallengeType.class, status.toUpperCase()))
            throw new BadRequestException("Invalid session status");
        challenge.setType(status.toUpperCase());

        var updatedChallenge = challengeRepo.save(challenge);
        return new ResponseMessage.SuccessResponseMessage("session status changed. Status: " + updatedChallenge.getType());
    }

    @Override
    public ResponseMessage update(UUID id, HelperDto.ChallengeCreateDto challengeCreateDto) {
        var challenge = challengeRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("challenge not found"));
        challengeRepo.findByName(challengeCreateDto.name()).ifPresent(chal -> {throw new ResourceExistsException("challenge with the name '" + chal.getName() + "' already exist");});

        if (!EnumUtils.isValidEnum(ChallengeType.class, challengeCreateDto.type().toUpperCase()))
            throw new BadRequestException("Invalid challenge type");

        challenge.setName(challengeCreateDto.name());
        challenge.setDescription(challenge.getDescription());
        challenge.setTarget(challengeCreateDto.target());

        challengeRepo.save(challenge);
        return new ResponseMessage.SuccessResponseMessage("challenge updated. Type: " + challenge.getType());
    }

    @Override
    public HelperDto.ChallengeFullDto getChallenge(UUID id) {
        var challenge = challengeRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("challenge not found"));

        return copyChallengeToDto(challenge);
    }

    @Override
    public List<HelperDto.ChallengeFullDto> getChallenges() {
        var challenges = challengeRepo.findAll();
        ArrayList challengeDto = new ArrayList<HelperDto.ChallengeFullDto>();

        challenges.forEach(session -> {challengeDto.add(copyChallengeToDto(session));});

        return challengeDto;
    }
    private static HelperDto.ChallengeFullDto copyChallengeToDto(Challenge challenge) {
        return HelperDto.ChallengeFullDto.builder()
                .id(challenge.getId())
                .name(challenge.getName())
                .type(challenge.getType())
                .target(challenge.getTarget())
                .sessions(challenge.getSessions())
                .challengeReports(challenge.getChallengeReports())
                .subscriptions(challenge.getSubscriptions())
                .updatedAt(challenge.getUpdatedAt())
                .createdAt(challenge.getCreatedAt())
                .build();
    }

}
