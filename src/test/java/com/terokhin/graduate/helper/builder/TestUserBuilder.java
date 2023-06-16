package com.terokhin.graduate.helper.builder;

import com.terokhin.graduate.model.dto.user.UserDto;
import com.terokhin.graduate.model.enity.User;
import com.terokhin.graduate.security.Role;
import org.modelmapper.ModelMapper;

import java.time.Instant;

public class TestUserBuilder {

    private User user;

    public TestUserBuilder() {
        user = new User();
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setTimeCreated(Instant.now());
        user.setRole(Role.USER);
    }

    public TestUserBuilder withId(Long id) {
        user.setId(id);
        return this;
    }

    public TestUserBuilder withEmail(String email) {
        user.setEmail(email);
        return this;
    }

    public TestUserBuilder withPassword(String password) {
        user.setPassword(password);
        return this;
    }

    public TestUserBuilder withNickname(String nickname) {
        user.setNickname(nickname);
        return this;
    }

    public TestUserBuilder withFirstName(String firstName) {
        user.setFirstName(firstName);
        return this;
    }

    public TestUserBuilder withLastName(String lastName) {
        user.setLastName(lastName);
        return this;
    }

    public TestUserBuilder withTimeCreated(Instant timeCreated) {
        user.setTimeCreated(timeCreated);
        return this;
    }

    public TestUserBuilder withRole(Role role) {
        user.setRole(role);
        return this;
    }

    public TestUserBuilder withBanned(boolean banned) {
        user.setBanned(banned);
        return this;
    }

    public User build() {
        return user;
    }

    public UserDto buildDto() {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(user, UserDto.class);
    }
}