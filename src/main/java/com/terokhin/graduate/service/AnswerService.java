package com.terokhin.graduate.service;

import com.terokhin.graduate.exception.ForbiddenAccessException;
import com.terokhin.graduate.model.dto.answer.AnswerDto;
import com.terokhin.graduate.model.dto.answer.CreateAnswerDto;
import com.terokhin.graduate.model.dto.answer.ModerateAnswerDto;
import com.terokhin.graduate.model.dto.answer.UpdateAnswerDto;
import com.terokhin.graduate.model.enity.Answer;
import com.terokhin.graduate.repository.AnswerRepository;
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
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final ModelMapper modelMapper;

    public AnswerDto getAnswer(long answerId) {
        var answer = findAnswerById(answerId);
        return answer.mapAnswerDto(answer, modelMapper);
    }

    public Page<AnswerDto> getAnswers(Pageable pageable, long questionId) {
        if (AccessService.isModerator()) {
            return answerRepository.findAllByQuestionId(pageable, questionId)
                    .map(answer -> answer.mapAnswerDto(answer, modelMapper));
        } else {
            return answerRepository.findAllByQuestionIdAndHiddenFalse(pageable, questionId)
                    .map(answer -> answer.mapAnswerDto(answer, modelMapper));
        }
    }

    @Transactional
    public AnswerDto createAnswer(CreateAnswerDto createAnswerDto) {
        var answer = new Answer();
        var timeCreated = Instant.now();

        modelMapper.map(createAnswerDto, answer);
        answer.setTimeCreated(timeCreated);
        answer.setUserId(AccessService.getCurrentUserId());

        answerRepository.save(answer);
        return answer.mapAnswerDto(answer, modelMapper);
    }

    @Transactional
    public AnswerDto updateAnswer(UpdateAnswerDto updateAnswerDto, long answerId) {
        var answer = findAnswerById(answerId);
        var timeModify = Instant.now();
        var currentUserId = AccessService.getCurrentUserId();

        if (answer.getUserId() != currentUserId) {
            throw new ForbiddenAccessException("User with id: %d have not access to update the answer with id: %d"
                    .formatted(currentUserId, answer.getId()));
        }
        modelMapper.map(updateAnswerDto, answer);
        answer.setTimeModify(timeModify);

        return answer.mapAnswerDto(answer, modelMapper);
    }

    @Transactional
    public AnswerDto moderateAnswer(ModerateAnswerDto moderateAnswerDto, long answerId) {
        var answer = findAnswerById(answerId);
        var timeModify = Instant.now();

        modelMapper.map(moderateAnswerDto, answer);
        answer.setTimeModify(timeModify);

        return answer.mapAnswerDto(answer, modelMapper);
    }

    public void deleteAnswer(long answerId) {
        var answer = findAnswerById(answerId);
        var currentUserId = AccessService.getCurrentUserId();

        if (answer.getUserId() != currentUserId) {
            throw new ForbiddenAccessException("User with id: %d have not access to delete the answer with id: %d"
                    .formatted(currentUserId, answer.getId()));
        }
        answerRepository.deleteById(answerId);
    }

    private Answer findAnswerById(long answerId) {
        return answerRepository.findById(answerId).orElseThrow(
                () -> new IllegalArgumentException("Answer with id: %d not found".formatted(answerId)));
    }

    public boolean hasAnswerByQuestionId(long questionId) {
        return answerRepository.existsByQuestionIdAndUserId(questionId, AccessService.getCurrentUserId());
    }
}
