package com.terokhin.graduate.repository;

import com.terokhin.graduate.model.enity.Question;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT DISTINCT q FROM Question q " +
           "WHERE (:searchText IS NULL OR " +
           "LOWER(q.title) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(q.description) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(:searchText) MEMBER OF q.tag) " +
           "AND q.hidden = false ")
    Page<Question> findAllWithSearchAndHidden(@Param("searchText") String searchText, Pageable pageable);

    @Query("SELECT DISTINCT q FROM Question q " +
           "WHERE (:searchText IS NULL OR " +
           "LOWER(q.title) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(q.description) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(:searchText) MEMBER OF q.tag) ")
    Page<Question> findAllWithSearch(@Param("searchText") String searchText, Pageable pageable);

    Page<Question> findAllByHiddenIsFalse(@NonNull Pageable pageable);
    
    @Override
    Page<Question> findAll(@NonNull Pageable pageable);
}
