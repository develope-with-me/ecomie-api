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
    ResponseMessage<Challenge> store(Challenge challenge);
    ResponseMessage<Challenge> changeType(UUID id, String status);

    ResponseMessage<Challenge> update(UUID id, Challenge challenge);
    Challenge getChallenge(UUID id);
    List<Challenge> getChallenges();

    ResponseMessage<Challenge> deleteChallenge(UUID id);
}
