package com.terokhin.graduate.exception;

import com.terokhin.graduate.util.AccessService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Instant;

import static org.slf4j.event.Level.DEBUG;
import static org.slf4j.event.Level.WARN;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@ControllerAdvice
public class ExceptionGlobalHandler {

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        var errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        return response(errorResponse, WARN);
    }

    @ExceptionHandler({IllegalStateException.class})
    public ResponseEntity<?> handleIllegalStateException(IllegalStateException e) {
        var errorResponse = new ErrorResponse(BAD_REQUEST, e.getMessage());
        return response(errorResponse, WARN);
    }

    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException e) {
        var errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
        return response(errorResponse, WARN);
    }

    @ExceptionHandler({ForbiddenAccessException.class})
    public ResponseEntity<?> handleForbiddenAccessException(ForbiddenAccessException ex) {
        var errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
        return response(errorResponse, DEBUG);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException ex) {
        var errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

    @ExceptionHandler({HttpClientErrorException.class})
    public ResponseEntity<?> handleRestClientException(HttpClientErrorException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getStatusCode());
    }

    private ResponseEntity<Object> response(ErrorResponse errorResponse, Level level) {
        var userPrincipal = AccessService.getUserPrincipal();
        var user = userPrincipal != null
                ? Long.toString(userPrincipal.getId())
                : "not logged in";
        var format = "Error response: {}, user {}, {}";
        var time = Instant.now();

        switch (level) {
            case DEBUG -> log.debug(format, errorResponse, user, time);
            case INFO -> log.info(format, errorResponse, user, time);
            case WARN -> log.warn(format, errorResponse, user, time);
            default -> throw new IllegalStateException("Unexpected value: " + level);
        }
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }
}
