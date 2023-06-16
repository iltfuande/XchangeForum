package com.terokhin.graduate.model.dto.user;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
public class UpdateUserDto {

    @Length(max = 100)
    private String password;

    @Length(max = 50)
    private String firstName;

    @Length(max = 50)
    private String lastName;
}
