package com.terokhin.graduate.helper;

import com.terokhin.graduate.model.enity.Question;
import com.terokhin.graduate.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionHelper {

    @Autowired
    private QuestionRepository questionRepository;

    public Question insertQuestion(Question newQuestion) {
        return questionRepository.save(newQuestion);
    }
}