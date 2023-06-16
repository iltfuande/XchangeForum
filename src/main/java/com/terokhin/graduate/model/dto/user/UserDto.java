package com.terokhin.graduate.model.dto.user;

import lombok.Data;

import java.time.Instant;

@Data
public class UserDto {

    private Long id;

    private String email;

    private String password;

    private String nickname;

    private String firstName;

    private String lastName;

    private Instant timeCreated;

    private boolean banned;
    
    private boolean deleted;
}
