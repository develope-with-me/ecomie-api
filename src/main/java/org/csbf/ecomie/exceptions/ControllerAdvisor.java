package org.csbf.ecomie.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class ControllerAdvisor  extends ResponseEntityExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(EcomieException.class)
    public ResponseEntity<Problem> handleEcomieException(EcomieException ex) {
        log.error("EcomieException: {}", ex.getMessage());
        Problem problem = ex.getProblem();
        return ResponseEntity
                .status(HttpStatus.valueOf(problem.statusCode()))
                .body(problem);
    }
}
