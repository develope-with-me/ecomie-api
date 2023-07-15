package org.csbf.security.service;

import org.csbf.security.utils.helperclasses.HelperDto;
import org.csbf.security.utils.helperclasses.ResponseMessage;

import java.util.List;
import java.util.UUID;

public interface ChallengeService {
    ResponseMessage store(HelperDto.ChallengeCreateDto challengeCreateDto);

//    ResponseMessage changeStatus(UUID id, String status);
//
//    ResponseMessage update(UUID id, HelperDto.SessionCreateDto sessionCreateDto);
//
//    HelperDto.SessionFullDto getSession(UUID id);
//
//    List<HelperDto.SessionFullDto> getSessions();
//
//    HelperDto.SessionFullDto assignChallenges(UUID sessionId, UUID[] challengeIds);
}
