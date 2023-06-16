package com.terokhin.graduate.model.dto.user.auth;

import com.terokhin.graduate.model.dto.user.UserDto;
import com.terokhin.graduate.security.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

    private String token;
    private Date tokenTimeExpiration;
    
    private UserDto userDto;
    
    private Role role;
}
