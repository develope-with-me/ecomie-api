package org.csbf.ecomie.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.csbf.ecomie.config.AuthContext;
import org.csbf.ecomie.constant.Role;
import org.csbf.ecomie.event.OnRoleChangeRequestEvent;
import org.csbf.ecomie.exceptions.BadRequestException;
import org.csbf.ecomie.service.AuthenticationService;
import org.csbf.ecomie.service.UserService;
import org.csbf.ecomie.utils.helperclasses.HelperDomain.*;
import org.csbf.ecomie.utils.helperclasses.ResponseMessage;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/secure")
@SecurityRequirement(name = "ApiKey")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "UserController", description = "This controller contains endpoints for users")
public class UserController {

    private final UserService userService;
    private final AuthenticationService authService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AuthContext authContext;


    @PutMapping(value = "/user/update", consumes = { "multipart/form-data" }, produces = { "application/json" })
    @Operation(summary = "Edit My Profile", description = "Modify currently authenticated user's profile information", tags = { "USER" })
    public ResponseEntity<ResponseMessage> updateAuthUserProfile( @RequestPart("file") Optional<MultipartFile> file, @RequestPart("json") MinimalUser user) {
        return new ResponseEntity<>(userService.updateAuthUserProfile(file, user), HttpStatus.CREATED);
    }

    @PutMapping(value = "/admin/update/users/{id}", consumes = { "multipart/form-data", "application/json" }, produces = { "application/json" })
    @Operation(summary = "Edit User Profile", description = "Modify user's profile information using his id", tags = { "ADMIN" })
    public ResponseEntity<ResponseMessage> updateUserProfile(@PathVariable(name = "id") UUID id, @RequestPart("file") Optional<MultipartFile> file, @RequestPart("json") User user) {
        return new ResponseEntity<>(userService.updateUserProfile(id, file, user), HttpStatus.CREATED);
    }

    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    @PutMapping(value = "/admin/update-user-role")
    @Operation(summary = "Assign New Role", description = "Change user's role using email", tags = { "ADMIN" })
    public ResponseMessage updateUserRole(@RequestBody UpdateUserRole user) { return userService.changeUserRole(user.email(), user.role()); }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/admin/users/{id}")
    @Operation(summary = "Get User Profile", description = "Get user's profile information using userId", tags = { "ADMIN" })
    public User getUserProfile(@PathVariable(name = "id") UUID id) { return userService.getUserProfile(id); }

//    @PreAuthorize("hasAuthority('USER') ")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/user/me")
    @Operation(summary = "Get My Profile", description = "Get currently authenticated user's information", tags = { "USER" })
    public MinimalUser getAuthUserProfile() {
        log.info("TEST: In Controller");

        return userService.getAuthUserProfile();
    }

    @GetMapping("/admin/user-pix/{id}")
    @Operation(summary = "Get My Profile Image", description = "Get user's profile image using userId", tags = { "ADMIN" })
    public ResponseEntity<byte[]> getUserProfileImage(@PathVariable(name = "id") UUID id) {
        return new ResponseEntity<>(userService.getUserProfilePicture(id), HttpStatus.OK);
    }

    @GetMapping("/user/my-pix")
    @Operation(summary = "Get User's Profile Image", description = "Get currently auth user's profile image", tags = { "USER" })
    public ResponseEntity<byte[]> getProfileImage() {
        return new ResponseEntity<>(userService.getProfilePicture(), HttpStatus.OK);
    }

    @DeleteMapping("/admin/user-pix/{id}/del")
    @Operation(summary = "Delete User Profile Image", description = "Delete user's profile image using userId", tags = { "ADMIN" })
    public ResponseEntity<ResponseMessage> deleteProfileImage(@PathVariable(name = "id") UUID id) {
        userService.deleteUserProfilePic(id);
        return new ResponseEntity<>(new ResponseMessage.SuccessResponseMessage("Image deleted"), HttpStatus.PARTIAL_CONTENT);
    }

    @DeleteMapping("/admin/del/users/{id}")
    @Operation(summary = "Delete User", description = "Delete user using userId", tags = { "ADMIN" })
    public ResponseEntity<ResponseMessage> hardDeleteProfile(@PathVariable(name = "id") UUID id) {
        userService.deleteUserProfile(id);
        return new ResponseEntity<>(new ResponseMessage.SuccessResponseMessage("User deleted"), HttpStatus.PARTIAL_CONTENT);
    }

    @PutMapping("/admin/soft-del/users/{id}")
    @Operation(summary = "Soft delete User", description = "Soft delete user using userId", tags = { "ADMIN" })
    public ResponseEntity<ResponseMessage> softDeleteProfile(@PathVariable(name = "id") UUID id) {
        return new ResponseEntity<>(userService.softDelete(id), HttpStatus.PARTIAL_CONTENT);
    }

    @PostMapping("/user/request-to-become/{role}")
    @Operation(summary = "Request For Access Clearance", description = "Request for higher clearance eg ECOMIEST, ADMIN, SPONSOR, MISSIONARY, etc", tags = { "USER" })
    public ResponseEntity<ResponseMessage> becomeAn( @PathVariable(name = "role") String role, HttpServletRequest servletRequest) {
        if (!EnumUtils.isValidEnum(Role.class, role.toUpperCase()))
            throw new BadRequestException("Invalid user role");
//        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        applicationEventPublisher.publishEvent(new OnRoleChangeRequestEvent(servletRequest.getHeader("host"), authContext.getAuthUser().getName(), role));

        return new ResponseEntity<>(new ResponseMessage.SuccessResponseMessage("Request sent successfully"), HttpStatus.OK);
    }

    @GetMapping("/admin/sessions/{sessionId}/users")
    @Operation(summary = "Get Session Users", description = "Get all blocked and/or unblocked users in a session", tags = { "ADMIN" })
    public ResponseEntity<List<User>> getUsersInASession(@PathVariable(name = "sessionId") UUID sessionId, @RequestParam("blocked") boolean blocked, @RequestParam("challenge") Optional<String> challengeId) {
        return new ResponseEntity<>(userService.getUsersInASession(sessionId, blocked, challengeId), HttpStatus.OK);
    }

    @GetMapping("/admin/users")
    @Operation(summary = "Get All Users", description = "Get all users in the system", tags = { "ADMIN" })
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @PostMapping("/admin/users")
    @Operation(summary = "Get All Users", description = "Get all users in the system", tags = { "ADMIN" })
    public ResponseEntity<AuthenticationResponse> creatUser(@RequestBody RegisterRequest request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }
}
