package com.terokhin.graduate.model.dto.question;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@Builder
public class CreateQuestionDto {

    @Length(min = 10, max = 100)
    @NotNull
    private String title;

    @Length(min = 20, max = 5_000)
    @NotNull
    private String description;

    @NotNull
    private @NotNull @NotEmpty List<@NotNull @NotEmpty String> tag;
}
