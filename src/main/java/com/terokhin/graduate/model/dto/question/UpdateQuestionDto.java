package com.terokhin.graduate.model.dto.question;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@Builder
public class UpdateQuestionDto {

    @Length(min = 10, max = 100)
    private String title;

    @Length(min = 20, max = 5_000)
    private String description;

    private @NotNull @NotEmpty List<@NotNull @NotEmpty String> tag;
}
