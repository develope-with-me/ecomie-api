package org.csbf.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csbf.security.service.UserService;
import org.csbf.security.utils.helperclasses.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/secure")
@RequiredArgsConstructor
public class UserController {

    UserService userService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/user/update", consumes = { "multipart/form-data" }, produces = { "application/json" })
    @Operation(summary = "Edit User Profile", description = "Modify currently authenticated user's profile information", tags = { "user" }
    )
    public ResponseEntity<ResponseMessage> updateAuthUserProfile( @RequestParam("image") Optional<MultipartFile> file, @RequestParam("jsonData") String jsonData) {
        return new ResponseEntity<>(userService.updateAuthUserProfile(file, jsonData), HttpStatus.CREATED);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/admin/update-user/{userId}", consumes = { "multipart/form-data" }, produces = { "application/json" })
    @Operation(summary = "Edit User Profile", description = "Modify currently authenticated user's profile information", tags = { "admin" }
    )
    public ResponseEntity<ResponseMessage> updateUserProfile(@PathVariable(name = "userId") UUID userId, @RequestParam("image") Optional<MultipartFile> file, @RequestParam("jsonData") String jsonData) {
        return new ResponseEntity<>(userService.updateUserProfile(userId, file, jsonData), HttpStatus.CREATED);
    }
}
