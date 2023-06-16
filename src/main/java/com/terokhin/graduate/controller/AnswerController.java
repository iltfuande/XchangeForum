package com.terokhin.graduate.controller;

import com.terokhin.graduate.model.dto.answer.AnswerDto;
import com.terokhin.graduate.model.dto.answer.CreateAnswerDto;
import com.terokhin.graduate.model.dto.answer.ModerateAnswerDto;
import com.terokhin.graduate.model.dto.answer.UpdateAnswerDto;
import com.terokhin.graduate.service.AnswerService;
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
@RequestMapping("/api/v1/answer")
public class AnswerController {

    private final AnswerService answerService;

    @GetMapping("/{answerId}")
    @Operation(summary = "Find the answer by answerId")
    public AnswerDto getAnswer(@PathVariable long answerId) {
        return answerService.getAnswer(answerId);
    }

    @GetMapping
    @Operation(summary = "Find all answers (under the question) by the questionId",
            description = "Pagination is used for searching. You can use " +
                          "'pageNumber' (0 - first page), 'pageSize' and 'sortBy' (any answer field)")
    public Page<AnswerDto> getAnswers(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam long questionId
    ) {
        var pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        return answerService.getAnswers(pageable, questionId);
    }

    @PostMapping
    @Operation(summary = "Create the answer")
    public AnswerDto createAnswer(@RequestBody @Valid CreateAnswerDto createAnswerDto) {
        var questionId = createAnswerDto.getQuestionId();
        var hasAnswer = answerService.hasAnswerByQuestionId(questionId);
        if (hasAnswer) {
            throw new IllegalArgumentException("You already give the answer for the question with id: %d".formatted(questionId));
        }

        return answerService.createAnswer(createAnswerDto);
    }

    @PutMapping("/{answerId}")
    @Operation(summary = "Update the answer by answerId", description = "Only for the user who created the answer")
    public AnswerDto updateAnswer(@RequestBody @Valid UpdateAnswerDto updateAnswerDto,
                                  @PathVariable long answerId) {
        return answerService.updateAnswer(updateAnswerDto, answerId);
    }

    @PreAuthorize("hasAuthority('MODERATOR')")
    @PutMapping("/{answerId}/moderate")
    @Operation(summary = "Moderate the answer by answerId", description = "Only for the moderator")
    public AnswerDto moderateAnswer(@RequestBody @Valid ModerateAnswerDto moderateAnswerDto,
                                    @PathVariable long answerId) {
        return answerService.moderateAnswer(moderateAnswerDto, answerId);
    }

    @DeleteMapping("/{answerId}")
    @Operation(summary = "Delete the answer by answerId", description = "Only for the user who created the answer")
    public ResponseEntity<String> deleteAnswer(@PathVariable long answerId) {
        answerService.deleteAnswer(answerId);
        return ResponseEntity.status(204).build();
    }
}
