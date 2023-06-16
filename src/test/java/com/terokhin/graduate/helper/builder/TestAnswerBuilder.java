package com.terokhin.graduate.helper.builder;

import com.terokhin.graduate.model.dto.answer.AnswerDto;
import com.terokhin.graduate.model.enity.Answer;
import org.modelmapper.ModelMapper;

import java.time.Instant;

public class TestAnswerBuilder {

    private Answer answer;

    public TestAnswerBuilder() {
        answer = new Answer();
        answer.setTimeCreated(Instant.now());
    }

    public TestAnswerBuilder withQuestionId(long questionId) {
        answer.setQuestionId(questionId);
        return this;
    }

    public TestAnswerBuilder withUserId(long userId) {
        answer.setUserId(userId);
        return this;
    }

    public TestAnswerBuilder withRespond(String respond) {
        answer.setRespond(respond);
        return this;
    }

    public TestAnswerBuilder withTimeCreated(Instant timeCreated) {
        answer.setTimeCreated(timeCreated);
        return this;
    }

    public TestAnswerBuilder withTimeModify(Instant timeModify) {
        answer.setTimeModify(timeModify);
        return this;
    }

    public TestAnswerBuilder withHidden(boolean hidden) {
        answer.setHidden(hidden);
        return this;
    }

    public Answer build() {
        return answer;
    }

    public AnswerDto buildDto(ModelMapper modelMapper) {
        return modelMapper.map(answer, AnswerDto.class);
    }
}