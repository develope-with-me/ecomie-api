package org.csbf.security.service.imp;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.csbf.security.service.ChallengeService;
import org.csbf.security.utils.helperclasses.HelperDto;
import org.csbf.security.utils.helperclasses.ResponseMessage;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengeServiceImp implements ChallengeService {

    @Override
    public ResponseMessage store(HelperDto.ChallengeCreateDto challengeCreateDto) {
        return null;
    }
}
