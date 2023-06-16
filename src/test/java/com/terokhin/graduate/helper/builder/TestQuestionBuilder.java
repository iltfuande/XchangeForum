package com.terokhin.graduate.helper.builder;

import com.terokhin.graduate.model.enity.Question;

import java.time.Instant;
import java.util.List;

public class TestQuestionBuilder {

    private Question question;
    
    public TestQuestionBuilder() {
        question = new Question();
        question.setTimeCreated(Instant.now());
    }

    public TestQuestionBuilder withCreatedUserId(long createdUserId) {
        question.setCreatedUserId(createdUserId);
        return this;
    }

    public TestQuestionBuilder withModeratorId(Long moderatorId) {
        question.setModeratorId(moderatorId);
        return this;
    }

    public TestQuestionBuilder withClose(boolean close) {
        question.setClose(close);
        return this;
    }

    public TestQuestionBuilder withTitle(String title) {
        question.setTitle(title);
        return this;
    }

    public TestQuestionBuilder withDescription(String description) {
        question.setDescription(description);
        return this;
    }

    public TestQuestionBuilder withTag(List<String> tag) {
        question.setTag(tag);
        return this;
    }

    public TestQuestionBuilder withViewed(int viewed) {
        question.setViewed(viewed);
        return this;
    }

    public TestQuestionBuilder withTimeCreated(Instant timeCreated) {
        question.setTimeCreated(timeCreated);
        return this;
    }

    public TestQuestionBuilder withTimeModify(Instant timeModify) {
        question.setTimeModify(timeModify);
        return this;
    }

    public TestQuestionBuilder withHidden(boolean hidden) {
        question.setHidden(hidden);
        return this;
    }

    public Question build() {
        return question;
    }
}
