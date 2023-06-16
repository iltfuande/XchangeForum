package com.terokhin.graduate.model.enity;

import com.terokhin.graduate.model.dto.question.QuestionDto;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.modelmapper.ModelMapper;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

;

@Getter
@Setter
@Entity
@ToString
public class Question implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_user_id")
    private long createdUserId;

    @Column(name = "moderator_id")
    private Long moderatorId;

    @Column
    private boolean close;

    @Length(min = 10, max = 100)
    @NotNull
    @Column
    private String title;

    @Length(min = 20, max = 5_000)
    @NotNull
    @Column
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column
    private List<String> tag;

    @Column
    private int viewed;

    @NotNull
    @Column
    private Instant timeCreated;

    @Column
    private Instant timeModify;

    @Column
    private boolean hidden;

    public QuestionDto mapQuestionDto(Question question, ModelMapper modelMapper) {
        return modelMapper.map(question, QuestionDto.class);
    }
}
