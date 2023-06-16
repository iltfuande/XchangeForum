package com.terokhin.graduate.model.dto.answer;

import lombok.Data;

import java.time.Instant;

@Data
public class AnswerDto {

    private Long id;

    private long questionId;

    private long userId;

    private String respond;

    private Instant timeCreated;

    private Instant timeModify;

    private boolean hidden;
}
