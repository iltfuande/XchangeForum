package com.terokhin.graduate.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
public class CreateUserDto {

    @Email
    @Length(max = 50)
    @NotNull
    private String email;

    @Length(max = 100)
    @NotNull
    private String password;

    @Length(max = 50)
    @NotNull
    private String nickname;

    @Length(max = 50)
    @NotNull
    private String firstName;

    @Length(max = 50)
    @NotNull
    private String lastName;
}
