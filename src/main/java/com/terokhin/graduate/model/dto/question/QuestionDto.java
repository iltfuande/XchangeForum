package com.terokhin.graduate.model.dto.question;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class QuestionDto {

    private Long id;

    private long createdUserId;

    private Long moderatorId;

    private boolean close;

    private String title;

    private String description;

    private List<String> tag;

    private int viewed;

    private Instant timeCreated;

    private Instant timeModify;

    private boolean hidden;
}
