package com.terokhin.graduate.service;

import com.terokhin.graduate.exception.ForbiddenAccessException;
import com.terokhin.graduate.model.dto.user.ModerateUserDto;
import com.terokhin.graduate.model.dto.user.UpdateUserDto;
import com.terokhin.graduate.model.dto.user.UserDto;
import com.terokhin.graduate.model.enity.User;
import com.terokhin.graduate.repository.UserRepository;
import com.terokhin.graduate.util.AccessService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDto getUser(long userId) {
        var user = findUserById(userId);

        if (AccessService.isViewer() || AccessService.getCurrentUserId() != userId
                                        && !AccessService.isModerator()) {
            user.setPassword(null);
            user.setEmail(null);
        }

        return user.mapUserDto(user, modelMapper);
    }

    public List<UserDto> getUsers() {
        var userDtos = userRepository.findAll().stream()
                .map(user -> user.mapUserDto(user, modelMapper))
                .toList();

        if (AccessService.isUser() || AccessService.isViewer()) {
            userDtos.forEach(userDto -> {
                userDto.setPassword(null);
                userDto.setEmail(null);
            });
        }
        return userDtos;
    }

    @Transactional
    public UserDto updateUser(UpdateUserDto updateUserDto, long userId) {
        var user = findUserById(userId);
        var currentUserId = AccessService.getCurrentUserId();

        if (!user.getId().equals(currentUserId)) {
            throw new ForbiddenAccessException("User with id: %d have not access to update the user with id: %d"
                    .formatted(currentUserId, user.getId()));
        }
        
        modelMapper.map(updateUserDto, user);
        
        if (updateUserDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
        }
        
        return user.mapUserDto(user, modelMapper);
    }

    @Transactional
    public UserDto moderateUser(ModerateUserDto moderateUserDto, long userId) {
        var user = findUserById(userId);
        modelMapper.map(moderateUserDto, user);

        if (moderateUserDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(moderateUserDto.getPassword()));
        }
        return user.mapUserDto(user, modelMapper);
    }

    @Transactional
    public void deleteUser(long userId) {
        var user = findUserById(userId);
        var currentUserId = AccessService.getCurrentUserId();

        if (!user.getId().equals(currentUserId)) {
            throw new ForbiddenAccessException("User with id: %d have not access to delete the user with id: %d"
                    .formatted(currentUserId, user.getId()));
        }
        
        user.setDeleted(true);
    }

    public boolean nicknameExists(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User with id: %d not found".formatted(userId)));
    }
}
