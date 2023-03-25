package org.csbf.security.controller;

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
public class TestController {
    @GetMapping("/demo-controller")
//    @RolesAllowed("USER")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello unauthenticated user");
    }

    @GetMapping("/secure/user")
//    @Role(1)
//    @RolesAllowed("USER")
    public ResponseEntity<String> sayHelloUser() {
        return ResponseEntity.ok("Hello authenticated USER");
    }

    @GetMapping("/secure/admin")
//    @Role(2)
//    @RolesAllowed("ADMIN")
    public ResponseEntity<String> sayHelloAdmin() throws InvalidTokenException {
        return ResponseEntity.ok("Hello authenticated ADMIN");
    }

}
