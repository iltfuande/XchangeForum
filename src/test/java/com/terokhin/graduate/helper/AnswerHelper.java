package com.terokhin.graduate.helper;

import com.terokhin.graduate.model.enity.Answer;
import com.terokhin.graduate.repository.AnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnswerHelper {

    @Autowired
    private AnswerRepository answerRepository;

    public Answer insertAnswer(Answer newAnswer) {
        return answerRepository.save(newAnswer);
    }
}