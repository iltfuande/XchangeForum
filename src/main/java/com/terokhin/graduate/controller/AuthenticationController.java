package com.terokhin.graduate.controller;

import com.terokhin.graduate.model.dto.user.CreateUserDto;
import com.terokhin.graduate.model.dto.user.auth.AuthenticationRequest;
import com.terokhin.graduate.model.dto.user.auth.AuthenticationResponse;
import com.terokhin.graduate.security.AuthenticationService;
import com.terokhin.graduate.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody CreateUserDto createUserDto) {
        var nickname = createUserDto.getNickname();
        var email = createUserDto.getEmail();
        
        if (userService.nicknameExists(nickname)) {
            log.info("Nickname is already registered {}", nickname);
            throw new HttpClientErrorException(BAD_REQUEST, "Nickname is already registered");
        }
        
        if (userService.emailExists(email)) {
            log.info("Email is already registered {}", email);
            throw new HttpClientErrorException(BAD_REQUEST, "Email is already registered");
        }
        
        return ResponseEntity.ok(authenticationService.register(createUserDto));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authentication(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
