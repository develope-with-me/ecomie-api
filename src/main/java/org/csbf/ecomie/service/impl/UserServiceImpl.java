package org.csbf.ecomie.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.csbf.ecomie.config.AuthContext;
import org.csbf.ecomie.constant.Role;
import org.csbf.ecomie.exceptions.*;
import org.csbf.ecomie.mapper.UserMapper;
import org.csbf.ecomie.entity.UserEntity;
import org.csbf.ecomie.repository.ChallengeRepository;
import org.csbf.ecomie.repository.SessionRepository;
import org.csbf.ecomie.repository.SubscriptionRepository;
import org.csbf.ecomie.repository.UserRepository;
import org.csbf.ecomie.service.FileUploadService;
import org.csbf.ecomie.service.UserService;
import org.csbf.ecomie.utils.commons.Mapper;
import org.csbf.ecomie.utils.helperclasses.HelperDomain.*;
import org.csbf.ecomie.utils.helperclasses.ResponseMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    private final SessionRepository sessionRepo;
    private final SubscriptionRepository subscriptionRepo;
    private final ChallengeRepository challengeRepo;
    private final AuthContext authContext;

    private final Environment environment;

    private final UserMapper mapper;

    @Override
    public ResponseMessage updateAuthUserProfile(
            Optional<MultipartFile> file,
            MinimalUser user
    ) {
        log.info("UserServiceImpl.updateAuthUserProfile");
        UserEntity userEntity = userRepository.findByEmail(authContext.getAuthUser().getName())
                .orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "User with email (%s) not found".formatted(authContext.getAuthUser().getName())).toException());

        var jsonUser = Mapper.toJsonObject(user);
        var newUser = Mapper.fromJsonObject(jsonUser, User.class);
        return getUpdateResponseMessage(file, newUser, userEntity);
    }

    @Override
    public ResponseMessage updateUserProfile(
            UUID userId,
            Optional<MultipartFile> file,
            User user
    ) {
        log.info("UserServiceImpl.updateUserProfile");
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "User with id (%s) not found".formatted(userId.toString())).toException());

        return getUpdateResponseMessage(file, user, userEntity);
    }

    @NotNull
    private ResponseMessage getUpdateResponseMessage(Optional<MultipartFile> file, User user, UserEntity userEntity) {

        String imageFileName;
        String oldFile = userEntity.getProfilePictureFileName();


        if (file.isPresent()) {
            try {
                imageFileName = fileUploadService.save(file.get());
                log.info("imageFileName: {}", imageFileName);
                log.info("Uploaded the file successfully");
            } catch (Exception e) {
                throw Problems.OBJECT_VALIDATION_ERROR.withProblemError("userEntity.profilePictureFileName", "Could not upload file!").toException();
            }
            if (oldFile != null && !imageFileName.equals(oldFile) && fileUploadService.resourceExist(oldFile)) {
                fileUploadService.deleteFile(oldFile);
            }

            log.info("old file name {}", oldFile);
            log.info("{}",
                    (imageFileName != null)
                            ? "image successfully uploaded"
                            : "image with name '" +
                            file.get().getOriginalFilename() +
                            "' already exist"
            );
            user = user.withProfilePictureFileName(imageFileName);

        }

        var oldDomain = mapper.asDomainObject(userEntity);
        var oldJsonUser = Mapper.toJsonObject(oldDomain);
        var newJsonUser = Mapper.toJsonObject(user);

        user = Mapper.withUpdateValuesOnly(oldJsonUser, newJsonUser, User.class);
        var entityToUpdate = mapper.asEntity(user);
        entityToUpdate.setPassword(userEntity.getPassword());

        userRepository.save(entityToUpdate);

        return new ResponseMessage.SuccessResponseMessage("Updated!");
    }

    @Override
    public ResponseMessage changeUserRole(String email, String role) {
        log.info("UserServiceImpl.changeUserRole");
        var user = userRepository.findByEmail(email).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "User with email (%s) not found".formatted(email)).toException());
        if (!EnumUtils.isValidEnum(Role.class, role.toUpperCase()))
            throw Problems.BAD_REQUEST.withProblemError("role", "Invalid user role").toException();
        if(!user.getRole().name().equals(role.toUpperCase())) {
            user.setRole(Role.valueOf(role.toUpperCase()));
            user = userRepository.save(user);
        }
        return new ResponseMessage.SuccessResponseMessage("User role updated - " + user.getRole());
    }


    @Override
    public User getUserProfile(UUID userId) {
        log.info("UserServiceImpl.getUserProfile");

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "User with id (%s) not found".formatted(userId.toString())).toException());
        return mapper.asDomainObject(userEntity);
    }

    @Override
    public MinimalUser getAuthUserProfile() {
        log.info("UserServiceImpl.getAuthUserProfile");
        UserEntity userEntity = userRepository.findByEmail(authContext.getAuthUser().getName())
                .orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "User with email (%s) not found".formatted(authContext.getAuthUser().getName())).toException());

        var jsonUser = Mapper.toJsonObject(userEntity);
        return Mapper.fromJsonObject(jsonUser, MinimalUser.class);
    }

    @Override
    public User getAuthUserProfile_v2() {
        log.info("UserServiceImpl.getAuthUserProfile");
        UserEntity userEntity = userRepository.findByEmail(authContext.getAuthUser().getName())
                .orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "User with email (%s) not found".formatted(authContext.getAuthUser().getName())).toException());

        return mapper.asDomainObject(userEntity).justMinimal();
    }

    @Override
    public void deleteUserProfilePic(UUID userId) {
        log.info("UserServiceImpl.deleteUserProfilePic");
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "User with id (%s) not found".formatted(userId.toString())).toException());
        String fileName = userEntity.getProfilePictureFileName();
        if (fileName == null) {
            throw Problems.NOT_FOUND.withProblemError("userEntity.profilePictureFileName", "User has no profile image").toException();
        }
        fileUploadService.deleteFile(fileName);
        userEntity.setProfilePictureFileName(null);
        userRepository.save(userEntity);
    }

//    @Override
//    public Resource loadUserImage(UUID userId) {
//        UserEntity userEntity = userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("UserEntity not found"));
//
//        String fileName = userEntity.getProfilePictureFileName();
//        if (fileName == null) return null;
//        return fileUploadService.load(fileName);
//    }

    @Override
    public Resource loadImage(EmailRequest emailRequest) {
        log.info("UserServiceImpl.loadImage");
        UserEntity userEntity = userRepository
                .findByEmail(emailRequest.email())
                .orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "User with email (%s) not found".formatted(emailRequest.email())).toException());
        String fileName = userEntity.getProfilePictureFileName();
        if (fileName == null) return null;
        return fileUploadService.load(fileName);
    }

    @Override
    public byte[] getProfilePicture() {

        UserEntity userEntity = userRepository
                .findByEmail(authContext.getAuthUser().getName())
                .orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "User with email (%s) not found".formatted(authContext.getAuthUser().getName())).toException());
        Resource resource = loadImage(new EmailRequest(userEntity.getEmail()));
        try {
            return resource == null ? null : resource.getInputStream().readAllBytes();
        } catch (Exception exception) {
            throw Problems.NOT_FOUND.withProblemError("userEntity.profilePictureFileName",  "Failed to extract user profile picture").toException();
        }
    }

    @Override
    public byte[] getUserProfilePicture(UUID userId) {
        UserEntity userEntity = userRepository
                .findById(userId)
                .orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "User with id (%s) not found".formatted(userId.toString())).toException());
        Resource resource = loadImage(new EmailRequest(userEntity.getEmail()));
        try {
            return resource == null ? null : resource.getInputStream().readAllBytes();
        } catch (Exception exception) {
            throw Problems.NOT_FOUND.withProblemError("userEntity.profilePictureFileName",  "Failed to extract user profile picture").toException();
        }
    }

    @Override
    public void deleteUserProfile(UUID userId) {
        userRepository.findById(userId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "User with id (%s) not found".formatted(userId.toString())).toException());
        userRepository.deleteById(userId);
    }

    // Soft delete userEntity account
    @Override
    public ResponseMessage softDelete(UUID userId) {

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "User with id (%s) not found".formatted(userId.toString())).toException());
//        userEntity.setAccountSoftDeleted(!userEntity.isAccountSoftDeleted());
        userEntity.setAccountSoftDeleted(!userEntity.getAccountSoftDeleted());
        userRepository.save(userEntity);

        return userEntity.getAccountSoftDeleted() ? new ResponseMessage.SuccessResponseMessage("Account soft deleted") : new ResponseMessage.SuccessResponseMessage("Account restored");
    }

    @Override
    public List<User> getUsersInASession(UUID sessionId, boolean blocked, Optional<String> optionalChallengeId) {
        var session = sessionRepo.findById(sessionId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "Session with id (%s) not found".formatted(sessionId.toString())).toException());
        List<UserEntity> userEntities = new ArrayList<>();

        if(optionalChallengeId.isPresent()) {
            String challengeIdString = optionalChallengeId.get();
            try {
                UUID challengeId = UUID.fromString(challengeIdString);
                var challenge = challengeRepo.findById(challengeId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("challengeEntity", "Challenge with id (%s) not found".formatted(challengeId.toString())).toException());
                userEntities = subscriptionRepo.selectAllUsersSubscribedToSessionViaChallenge(session, challenge);
            }catch (IllegalArgumentException e) {
                throw Problems.INVALID_PARAMETER_ERROR.withProblemError("challengeId", "Invalid challengeId (%s)".formatted(challengeIdString)).toException();
            }
        } else if(blocked) {
            userEntities = subscriptionRepo.selectAllUsersBlockedInSession(session);
        } else {
            userEntities = subscriptionRepo.selectAllUsersSubscribedToSession(session);

        }
        return mapper.asDomainObjects(userEntities);
    }

    @Override
    public List<User> getAllUsers() {
        return mapper.asDomainObjects(userRepository.findAll());
    }
    /**
     * / Create userEntity basic userEntity profile details from auth service route
     */


}
