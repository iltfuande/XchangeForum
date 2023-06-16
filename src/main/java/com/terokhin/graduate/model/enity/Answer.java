package com.terokhin.graduate.model.enity;

import com.terokhin.graduate.model.dto.answer.AnswerDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@Entity
@ToString
public class Answer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_id")
    private long questionId;

    @Column(name = "user_id")
    private long userId;

    @NotNull
    @Column
    private String respond;

    @NotNull
    @Column
    private Instant timeCreated;

    @Column
    private Instant timeModify;

    @Column
    private boolean hidden;

    public AnswerDto mapAnswerDto(Answer answer, ModelMapper modelMapper) {
        return modelMapper.map(answer, AnswerDto.class);
    }
}
