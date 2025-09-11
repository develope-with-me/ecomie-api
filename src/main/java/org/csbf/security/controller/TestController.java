package org.csbf.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;
import org.csbf.security.exceptions.InvalidTokenException;
import org.springframework.context.annotation.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/api/v1")
@SecurityRequirement(name = "api")
public class TestController {
    @GetMapping("/demo-controller")
    @Operation(summary = "Unprotected test endpoint", description = "Endpoint to test unsecure request", tags = {"test"})
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello unauthenticated userEntity");
    }

    @GetMapping("/secure/user")
    @Operation(summary = "UserEntity test endpoint", description = "Endpoint to test userEntity role", tags = {"test"})
    public ResponseEntity<String> sayHelloUser() { return ResponseEntity.ok("Hello authenticated USER"); }

    @GetMapping("/secure/admin")
    @Operation(summary = "Admin test endpoint", description = "Endpoint to test admin role", tags = {"test"})
    public ResponseEntity<String> sayHelloAdmin() throws InvalidTokenException {
        return ResponseEntity.ok("Hello authenticated ADMIN");
    }

}
