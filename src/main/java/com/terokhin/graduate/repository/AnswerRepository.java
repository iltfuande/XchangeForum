package com.terokhin.graduate.repository;

import com.terokhin.graduate.model.enity.Answer;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    Page<Answer> findAllByQuestionId(@NonNull Pageable pageable, long questionId);
    
    Page<Answer> findAllByQuestionIdAndHiddenFalse(@NonNull Pageable pageable, long questionId);

    boolean existsByQuestionIdAndUserId(long questionId, Long currentUserId);

    void removeAllByQuestionId(long questionId);
}
