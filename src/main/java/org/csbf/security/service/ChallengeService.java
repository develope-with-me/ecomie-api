package org.csbf.security.service;

import org.csbf.security.utils.helperclasses.HelperDomain.*;
import org.csbf.security.utils.helperclasses.ResponseMessage;

import java.util.List;
import java.util.UUID;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
public interface ChallengeService {
    ResponseMessage store(Challenge challenge);
    ResponseMessage changeType(UUID id, String status);

    ResponseMessage update(UUID id, Challenge challenge);
    Challenge getChallenge(UUID id);
    List<Challenge> getChallenges();
}
