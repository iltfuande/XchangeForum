package com.terokhin.graduate.controller;

import com.terokhin.graduate.model.dto.question.CreateQuestionDto;
import com.terokhin.graduate.model.dto.question.ModerateQuestionDto;
import com.terokhin.graduate.model.dto.question.QuestionDto;
import com.terokhin.graduate.model.dto.question.UpdateQuestionDto;
import com.terokhin.graduate.service.AnswerService;
import com.terokhin.graduate.service.QuestionService;
import com.terokhin.graduate.util.AccessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/question")
public class QuestionController {

    private final QuestionService questionService;
    private final AnswerService answerService;

    @GetMapping("/{questionId}")
    @Operation(summary = "Find the question by questionId")
    public QuestionDto getQuestion(@PathVariable long questionId) {
        return questionService.getQuestion(questionId);
    }

    @GetMapping
    @Operation(summary = "Find all questions",
            description = "Pagination is used for searching. You can use " +
                          "'pageNumber' (0 - first page), 'pageSize', 'sortBy' (any question field), " +
                          "and 'order' - DESC ASC")
    public Page<QuestionDto> getQuestions(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String order,
            @RequestParam(required = false) String searchText
    ) {
        var pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.fromString(order), sortBy));
        return questionService.getQuestions(searchText, pageable);
    }

    @PostMapping
    @Operation(summary = "Create the question")
    public QuestionDto createQuestion(@RequestBody @Valid CreateQuestionDto createQuestionDto) {
        return questionService.createQuestion(createQuestionDto);
    }

    @PutMapping("/{questionId}")
    @Operation(summary = "Update the question by questionId",
            description = "Only for the user who created the question")
    public QuestionDto updateQuestion(@RequestBody @Valid UpdateQuestionDto updateQuestionDto,
                                      @PathVariable long questionId) {
        return questionService.updateQuestion(updateQuestionDto, questionId);
    }

    @PreAuthorize("hasAuthority('MODERATOR')")
    @PutMapping("/{questionId}/moderate")
    @Operation(summary = "Moderate the question by questionId", description = "Only for the moderator")
    public QuestionDto moderateQuestion(@RequestBody @Valid ModerateQuestionDto moderateQuestionDto,
                                        @PathVariable long questionId) {
        return questionService.moderateQuestion(moderateQuestionDto, questionId);
    }

    @GetMapping("/{questionId}/answer")
    @Operation(summary = "Can user give answer", description = "Checks that current user can give answer for the question by id. " +
                                                               "If it`s viewer - returns false")
    public boolean canUserAnswer(@PathVariable long questionId) {
        var currentUserId = AccessService.getCurrentUserId();
        if (currentUserId == null) {
            return false;
        }
        return !answerService.hasAnswerByQuestionId(questionId);
    }

    @DeleteMapping("/{questionId}")
    @Operation(summary = "Delete the question by questionId",
            description = "Only for the user who created the question")
    public ResponseEntity<String> deleteQuestion(@PathVariable long questionId) {
        questionService.deleteQuestion(questionId);
        return ResponseEntity.status(204).build();
    }
}
