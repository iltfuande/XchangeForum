package com.terokhin.graduate.controller;

import com.terokhin.graduate.model.dto.user.ModerateUserDto;
import com.terokhin.graduate.model.dto.user.UpdateUserDto;
import com.terokhin.graduate.model.dto.user.UserDto;
import com.terokhin.graduate.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    @Operation(summary = "Get the user by id")
    public UserDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update the user by userId", description = "Only for the user")
    public UserDto updateUser(@RequestBody @Valid UpdateUserDto updateUserDto,
                              @PathVariable long userId) {
        return userService.updateUser(updateUserDto, userId);
    }

    @PreAuthorize("hasAuthority('MODERATOR')")
    @PutMapping("/{userId}/moderate")
    @Operation(summary = "Moderate the user by userId", description = "Only for the moderator")
    public UserDto moderateUser(@RequestBody @Valid ModerateUserDto moderateUserDto,
                                @PathVariable long userId) {
        return userService.moderateUser(moderateUserDto, userId);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete the user by userId", description = "Only for the user")
    public ResponseEntity<String> deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.status(204).build();
    }
}
