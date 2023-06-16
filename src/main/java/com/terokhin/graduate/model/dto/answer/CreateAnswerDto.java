package com.terokhin.graduate.model.dto.answer;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
public class CreateAnswerDto {

    private long questionId;

    @NotNull
    @Length(min = 20, max = 5_000)
    private String respond;
}
