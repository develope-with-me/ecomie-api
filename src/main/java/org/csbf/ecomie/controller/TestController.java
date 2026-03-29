package org.csbf.ecomie.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.csbf.ecomie.exceptions.InvalidTokenException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.api.base.url}")
@SecurityRequirement(name = "api")
public class TestController {
    @GetMapping("/health")
    @Operation(summary = "Health Check", description = "Health check end point", tags = {"test"})
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Application up and running");
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
