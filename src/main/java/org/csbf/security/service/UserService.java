package org.csbf.security.service;

import org.csbf.security.utils.helperclasses.HelperDomain.*;
import org.csbf.security.utils.helperclasses.ResponseMessage;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
public interface UserService {

    ResponseMessage updateAuthUserProfile(
            Optional<MultipartFile> file,
            MinimalUser user
    );

    ResponseMessage updateUserProfile(
            UUID userId,
            Optional<MultipartFile> file,
            User user
    );

    ResponseMessage changeUserRole(String email, String role);

    User getUserProfile(UUID userId);

    MinimalUser getAuthUserProfile();

    void deleteUserProfilePic(UUID userId);

    Resource loadImage(EmailRequest emailRequest);

    byte[] getProfilePicture();

    byte[] getUserProfilePicture(UUID userId);

    // Soft delete userEntity account
    ResponseMessage softDelete(UUID userId);

    void deleteUserProfile(UUID userId);

    List<User> getUsersInASession(UUID sessionId, boolean blocked, Optional<String> optionalChallengeId);

//    List<HelperDomain.UserFullDto> getUsersSubscribedToSessionViaChallenge(UUID sessionId, UUID challengeId);

    List<User> getAllUsers();
}
