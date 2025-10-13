package org.csbf.security.service.imp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.csbf.security.config.AuthContext;
import org.csbf.security.constant.Role;
import org.csbf.security.exceptions.*;
import org.csbf.security.mapper.UserMapper;
import org.csbf.security.entity.UserEntity;
import org.csbf.security.repository.ChallengeRepository;
import org.csbf.security.repository.SessionRepository;
import org.csbf.security.repository.SubscriptionRepository;
import org.csbf.security.repository.UserRepository;
import org.csbf.security.service.FileUploadService;
import org.csbf.security.service.UserService;
import org.csbf.security.utils.commons.Mapper;
import org.csbf.security.utils.helperclasses.HelperDomain.*;
import org.csbf.security.utils.helperclasses.ResponseMessage;
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
public class UserServiceImp implements UserService {

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
        log.info("UserServiceImp.updateAuthUserProfile");
        UserEntity userEntity = userRepository.findByEmail(authContext.getAuthUser().getName())
                .orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "UserEntity with email (%s) not found".formatted(authContext.getAuthUser().getName())).toException());

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
        log.info("UserServiceImp.updateUserProfile");
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "UserEntity with id (%s) not found".formatted(userId.toString())).toException());

        return getUpdateResponseMessage(file, user, userEntity);
    }

    @NotNull
    private ResponseMessage getUpdateResponseMessage(Optional<MultipartFile> file, User user, UserEntity userEntity) {
        UserEntity entityToUpdate = mapper.toEntity(user);

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
            entityToUpdate.setProfilePictureFileName(imageFileName);

        }
        var oldJsonUser = Mapper.toJsonObject(userEntity);
        var newJsonUser = Mapper.toJsonObject(entityToUpdate);
        oldJsonUser.putAll(newJsonUser);
        entityToUpdate = Mapper.fromJsonObject(oldJsonUser, UserEntity.class);

        userRepository.save(entityToUpdate);

        return new ResponseMessage.SuccessResponseMessage("Updated!");
    }

    @Override
    public ResponseMessage changeUserRole(String email, String role) {
        log.info("UserServiceImp.changeUserRole");
        var user = userRepository.findByEmail(email).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "UserEntity with email (%s) not found".formatted(email)).toException());
        if (!EnumUtils.isValidEnum(Role.class, role.toUpperCase()))
            throw Problems.BAD_REQUEST.withProblemError("role", "Invalid user role").toException();
        if(!user.getRole().name().equals(role.toUpperCase())) {
            user.setRole(Role.valueOf(role.toUpperCase()));
            user = userRepository.save(user);
        }
        return new ResponseMessage.SuccessResponseMessage("UserEntity role updated - " + user.getRole());
    }


    @Override
    public User getUserProfile(UUID userId) {
        log.info("UserServiceImp.getUserProfile");

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "UserEntity with id (%s) not found".formatted(userId.toString())).toException());
        return mapper.asDomainObject(userEntity);
    }

    @Override
    public MinimalUser getAuthUserProfile() {
        log.info("UserServiceImp.getAuthUserProfile");
        UserEntity userEntity = userRepository.findByEmail(authContext.getAuthUser().getName())
                .orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "UserEntity with email (%s) not found".formatted(authContext.getAuthUser().getName())).toException());

        var jsonUser = Mapper.toJsonObject(userEntity);
        return Mapper.fromJsonObject(jsonUser, MinimalUser.class);
//        return new MinimalUser(userEntity);
    }

    @Override
    public void deleteUserProfilePic(UUID userId) {
        log.info("UserServiceImp.deleteUserProfilePic");
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "UserEntity with id (%s) not found".formatted(userId.toString())).toException());
        String fileName = userEntity.getProfilePictureFileName();
        if (fileName == null) {
            throw Problems.NOT_FOUND.withProblemError("userEntity.profilePictureFileName", "UserEntity has no profile image").toException();
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
        log.info("UserServiceImp.loadImage");
        UserEntity userEntity = userRepository
                .findByEmail(emailRequest.email())
                .orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "UserEntity with email (%s) not found".formatted(emailRequest.email())).toException());
        String fileName = userEntity.getProfilePictureFileName();
        if (fileName == null) return null;
        return fileUploadService.load(fileName);
    }

    @Override
    public byte[] getProfilePicture() {

        UserEntity userEntity = userRepository
                .findByEmail(authContext.getAuthUser().getName())
                .orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "UserEntity with email (%s) not found".formatted(authContext.getAuthUser().getName())).toException());
        Resource resource = loadImage(new EmailRequest(userEntity.getEmail()));
        try {
            return resource == null ? null : resource.getInputStream().readAllBytes();
        } catch (Exception exception) {
            throw Problems.NOT_FOUND.withProblemError("userEntity.profilePictureFileName",  "Failed to extract userEntity profile picture").toException();
        }
    }

    @Override
    public byte[] getUserProfilePicture(UUID userId) {
        UserEntity userEntity = userRepository
                .findById(userId)
                .orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "UserEntity with id (%s) not found".formatted(userId.toString())).toException());
        Resource resource = loadImage(new EmailRequest(userEntity.getEmail()));
        try {
            return resource == null ? null : resource.getInputStream().readAllBytes();
        } catch (Exception exception) {
            throw Problems.NOT_FOUND.withProblemError("userEntity.profilePictureFileName",  "Failed to extract userEntity profile picture").toException();
        }
    }

    @Override
    public void deleteUserProfile(UUID userId) {
        userRepository.findById(userId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "UserEntity with id (%s) not found".formatted(userId.toString())).toException());
        userRepository.deleteById(userId);
    }

    // Soft delete userEntity account
    @Override
    public ResponseMessage softDelete(UUID userId) {

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "UserEntity with id (%s) not found".formatted(userId.toString())).toException());
        userEntity.setAccountSoftDeleted(!userEntity.isAccountSoftDeleted());
        userRepository.save(userEntity);

        return userEntity.isAccountSoftDeleted() ? new ResponseMessage.SuccessResponseMessage("Account soft deleted") : new ResponseMessage.SuccessResponseMessage("Account restored");
    }

    @Override
    public List<User> getUsersInASession(UUID sessionId, boolean blocked, Optional<String> optionalChallengeId) {
        var session = sessionRepo.findById(sessionId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "SessionEntity with id (%s) not found".formatted(sessionId.toString())).toException());
        List<UserEntity> userEntities = new ArrayList<>();

        if(optionalChallengeId.isPresent()) {
            String challengeIdString = optionalChallengeId.get();
            try {
                UUID challengeId = UUID.fromString(challengeIdString);
                var challenge = challengeRepo.findById(challengeId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("challengeEntity", "ChallengeEntity with id (%s) not found".formatted(challengeId.toString())).toException());
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
        List<UserFullDto> users = new ArrayList<>();
        return mapper.asDomainObjects(userRepository.findAll());
    }
    /**
     * / Create userEntity basic userEntity profile details from auth service route
     */


}
