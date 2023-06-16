package com.terokhin.graduate.service;

import com.terokhin.graduate.exception.ForbiddenAccessException;
import com.terokhin.graduate.model.dto.question.CreateQuestionDto;
import com.terokhin.graduate.model.dto.question.ModerateQuestionDto;
import com.terokhin.graduate.model.dto.question.QuestionDto;
import com.terokhin.graduate.model.dto.question.UpdateQuestionDto;
import com.terokhin.graduate.model.enity.Question;
import com.terokhin.graduate.repository.AnswerRepository;
import com.terokhin.graduate.repository.QuestionRepository;
import com.terokhin.graduate.util.AccessService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@AllArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ModelMapper modelMapper;

    public QuestionDto getQuestion(long questionId) {
        var question = findQuestionById(questionId);
        return question.mapQuestionDto(question, modelMapper);
    }

    public Page<QuestionDto> getQuestions(String searchText, Pageable pageable) {
        if ((searchText != null && searchText.equals("")) && AccessService.isModerator()) {
            return questionRepository.findAll(pageable)
                    .map(question -> question.mapQuestionDto(question, modelMapper));
        } else if (searchText != null && searchText.equals("")) {
            return questionRepository.findAllByHiddenIsFalse(pageable)
                    .map(question -> question.mapQuestionDto(question, modelMapper));
        } else if (AccessService.isModerator()) {
            return questionRepository.findAllWithSearch(searchText, pageable)
                    .map(question -> question.mapQuestionDto(question, modelMapper));
        } else {
            return questionRepository.findAllWithSearchAndHidden(searchText, pageable)
                    .map(question -> question.mapQuestionDto(question, modelMapper));
        }
    }

    @Transactional
    public QuestionDto createQuestion(CreateQuestionDto createQuestionDto) {
        var question = new Question();
        var timeCreated = Instant.now();

        modelMapper.map(createQuestionDto, question);
        question.setTimeCreated(timeCreated);
        question.setCreatedUserId(AccessService.getCurrentUserId());

        questionRepository.save(question);
        return question.mapQuestionDto(question, modelMapper);
    }

    @Transactional
    public QuestionDto updateQuestion(UpdateQuestionDto updateQuestionDto, long questionId) {
        var question = findQuestionById(questionId);
        var timeModify = Instant.now();
        var currentUserId = AccessService.getCurrentUserId();

        if (question.getCreatedUserId() != currentUserId) {
            throw new ForbiddenAccessException("User with id: %d have not access to update the question with id: %d"
                    .formatted(currentUserId, question.getId()));
        }
        modelMapper.map(updateQuestionDto, question);
        question.setTimeModify(timeModify);

        return question.mapQuestionDto(question, modelMapper);
    }

    @Transactional
    public QuestionDto moderateQuestion(ModerateQuestionDto moderateQuestionDto, long questionId) {
        var question = findQuestionById(questionId);
        var timeModify = Instant.now();

        modelMapper.map(moderateQuestionDto, question);
        question.setTimeModify(timeModify);
        question.setModeratorId(AccessService.getCurrentUserId());

        return question.mapQuestionDto(question, modelMapper);
    }

    @Transactional
    public void deleteQuestion(long questionId) {
        var question = findQuestionById(questionId);
        var currentUserId = AccessService.getCurrentUserId();

        if (question.getCreatedUserId() != currentUserId) {
            throw new ForbiddenAccessException("User with id: %d have not access to delete the question with id: %d"
                    .formatted(currentUserId, question.getId()));
        }
        answerRepository.removeAllByQuestionId(questionId);
        questionRepository.deleteById(questionId);
    }

    private Question findQuestionById(long questionId) {
        return questionRepository.findById(questionId).orElseThrow(
                () -> new IllegalArgumentException("Question with id: %d not found".formatted(questionId)));
    }
}
