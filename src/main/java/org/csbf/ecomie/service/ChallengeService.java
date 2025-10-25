package org.csbf.ecomie.service;

import org.csbf.ecomie.utils.helperclasses.HelperDomain.*;
import org.csbf.ecomie.utils.helperclasses.ResponseMessage;

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
