package com.terokhin.graduate.security;

import lombok.Data;

@Data
public class UserPrincipal {
    private Long id;
    private String nickname;
    private String email;
    private Role role;
}