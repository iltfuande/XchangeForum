package com.terokhin.graduate.security;

import com.terokhin.graduate.model.dto.user.auth.AuthenticationRequest;
import com.terokhin.graduate.model.dto.user.auth.AuthenticationResponse;
import com.terokhin.graduate.model.dto.user.CreateUserDto;
import com.terokhin.graduate.model.enity.User;
import com.terokhin.graduate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;

    @Transactional
    public AuthenticationResponse register(CreateUserDto createUserDto) {
        var user = new User();
        var timeCreated = Instant.now();

        modelMapper.map(createUserDto, user);
        user.setTimeCreated(timeCreated);
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(createUserDto.getPassword()));

        userRepository.save(user);
        
        var token = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .tokenTimeExpiration(jwtService.extractExpiration(token))
                .userDto(user.mapUserDto(user, modelMapper))
                .role(user.getRole())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var nickname = request.getNickname();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(nickname, request.getPassword())
        );
        var user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UsernameNotFoundException(nickname));
        
        var token = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .tokenTimeExpiration(jwtService.extractExpiration(token))
                .userDto(user.mapUserDto(user, modelMapper))
                .role(user.getRole())
                .build();
    }
}
