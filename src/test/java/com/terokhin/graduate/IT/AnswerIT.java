package com.terokhin.graduate.IT;

import com.terokhin.graduate.helper.builder.TestAnswerBuilder;
import com.terokhin.graduate.helper.builder.TestQuestionBuilder;
import com.terokhin.graduate.helper.builder.TestUserBuilder;
import com.terokhin.graduate.model.dto.answer.AnswerDto;
import com.terokhin.graduate.model.dto.answer.CreateAnswerDto;
import com.terokhin.graduate.model.dto.answer.ModerateAnswerDto;
import com.terokhin.graduate.model.dto.answer.UpdateAnswerDto;
import com.terokhin.graduate.security.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnswerIT extends BaseIT {

    private static final String GET_ANSWER_ENDPOINT = "/api/v1/answer/{answerId}";
    private static final String CREATE_ANSWER_ENDPOINT = "/api/v1/answer";
    private static final String UPDATE_ANSWER_ENDPOINT = "/api/v1/answer/{answerId}";
    private static final String MODERATE_ANSWER_ENDPOINT = "/api/v1/answer/{answerId}/moderate";
    private static final String DELETE_ANSWER_ENDPOINT = "/api/v1/answer/{answerId}";

    @Nested
    @DisplayName("GET request to \"" + GET_ANSWER_ENDPOINT + "\"")
    class GetAnswerTest {

        @Test
        @DisplayName("returns answer")
        void getAnswerShouldReturnOkStatus() {
            // Given
            var user = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("user")
                    .withEmail("user@gmail.com")
                    .build()
            );
            var question = questionHelper.insertQuestion(new TestQuestionBuilder()
                    .withTag(List.of("java", "docker"))
                    .withTitle("Some title for the question12345")
                    .withDescription("Some description for the question12345")
                    .withCreatedUserId(user.getId())
                    .build()
            );
            var answer = answerHelper.insertAnswer(new TestAnswerBuilder()
                    .withQuestionId(question.getId())
                    .withUserId(user.getId())
                    .withRespond("Some respond on the question")
                    .build()
            );

            // When
            addAuthorizationHeader(user);
            var response = restTemplate.exchange(GET_ANSWER_ENDPOINT, HttpMethod.GET, HttpEntity.EMPTY, AnswerDto.class, answer.getId());

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertThat(response.getBody()).isNotNull();

            var body = response.getBody();
            assertThat(body.getId()).isEqualTo(answer.getId());
            assertThat(body.getUserId()).isEqualTo(user.getId());
            assertThat(body.getQuestionId()).isEqualTo(question.getId());
            assertThat(body.getRespond()).isEqualTo(answer.getRespond());
            assertThat(body.getTimeModify()).isNull();
            assertThat(body.getTimeCreated()).isNotNull();
            assertThat(body.isHidden()).isFalse();
        }

        @Test
        @DisplayName("returns status BAD_REQUEST if answerId does not exist")
        void getAnswerShouldReturnBadRequestStatusIfAnswerIdDoesNotExist() {
            // Given
            var user = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("user")
                    .withEmail("user@gmail.com")
                    .build()
            );
            var question = questionHelper.insertQuestion(new TestQuestionBuilder()
                    .withTag(List.of("java", "docker"))
                    .withTitle("Some title for the question12345")
                    .withDescription("Some description for the question12345")
                    .withCreatedUserId(user.getId())
                    .build()
            );
            var answer = answerHelper.insertAnswer(new TestAnswerBuilder()
                    .withQuestionId(question.getId())
                    .withUserId(user.getId())
                    .withRespond("Some respond on the question")
                    .build()
            );
            long nonExistingAnswerId = answer.getId() + 1L;

            // When
            addAuthorizationHeader(user);
            var response = restTemplate.exchange(GET_ANSWER_ENDPOINT, HttpMethod.GET, HttpEntity.EMPTY, Void.class, nonExistingAnswerId);

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @DisplayName("POST request to \"" + CREATE_ANSWER_ENDPOINT + "\"")
    @Nested
    class CreateAnswerTest {

        @Test
        @DisplayName("returns status OK and create answer")
        void createAnswerShouldReturnOkStatus() {
            // Given
            var user = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("user")
                    .withEmail("user@gmail.com")
                    .build()
            );
            var question = questionHelper.insertQuestion(new TestQuestionBuilder()
                    .withTag(List.of("java", "docker"))
                    .withTitle("Some title for the question12345")
                    .withDescription("Some description for the question12345")
                    .withCreatedUserId(user.getId())
                    .build()
            );
            var createAnswerDto = CreateAnswerDto.builder()
                    .questionId(question.getId())
                    .respond("Some respond on the question")
                    .build();

            // When
            addAuthorizationHeader(user);
            var response = restTemplate.exchange(CREATE_ANSWER_ENDPOINT, HttpMethod.POST, new HttpEntity<>(createAnswerDto), AnswerDto.class);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertThat(response.getBody()).isNotNull();

            var body = response.getBody();
            assertThat(body.getId()).isNotNull();
            assertThat(body.getUserId()).isEqualTo(user.getId());
            assertThat(body.getQuestionId()).isEqualTo(question.getId());
            assertThat(body.getRespond()).isEqualTo(createAnswerDto.getRespond());
            assertThat(body.getTimeModify()).isNull();
            assertThat(body.getTimeCreated()).isNotNull();
            assertThat(body.isHidden()).isFalse();
        }
    }

    @DisplayName("PUT request to \"" + UPDATE_ANSWER_ENDPOINT + "\"")
    @Nested
    class UpdateAnswerTest {

        @Test
        @DisplayName("returns status OK if answer update by the user")
        void updateAnswerShouldReturnOkStatus() {
            // Given
            var user = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("user")
                    .withEmail("user@gmail.com")
                    .build()
            );
            var question = questionHelper.insertQuestion(new TestQuestionBuilder()
                    .withTag(List.of("java", "docker"))
                    .withTitle("Some title for the question12345")
                    .withDescription("Some description for the question12345")
                    .withCreatedUserId(user.getId())
                    .build()
            );
            var answer = answerHelper.insertAnswer(new TestAnswerBuilder()
                    .withQuestionId(question.getId())
                    .withUserId(user.getId())
                    .withRespond("Some respond on the question")
                    .build()
            );
            var updateAnswerDto = UpdateAnswerDto.builder()
                    .respond("Some respond on the question1234")
                    .build();

            // When
            addAuthorizationHeader(user);
            var response = restTemplate.exchange(UPDATE_ANSWER_ENDPOINT, HttpMethod.PUT, new HttpEntity<>(updateAnswerDto), AnswerDto.class, answer.getId());

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertThat(response.getBody()).isNotNull();

            var body = response.getBody();
            assertThat(body.getId()).isEqualTo(answer.getId());
            assertThat(body.getUserId()).isEqualTo(user.getId());
            assertThat(body.getQuestionId()).isEqualTo(question.getId());
            assertThat(body.getRespond()).isEqualTo(updateAnswerDto.getRespond());
            assertThat(body.getTimeModify()).isNotNull();
            assertThat(body.getTimeCreated()).isNotNull();
            assertThat(body.isHidden()).isFalse();
        }

        @Test
        @DisplayName("returns status FORBIDDEN if answer tries to update by another user")
        void updateAnswerShouldReturnForbiddenStatusIfAnotherUserTriesUpdate() {
            // Given
            var user = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("user")
                    .withEmail("user@gmail.com")
                    .build()
            );
            var user2 = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("user2")
                    .withEmail("user2@gmail.com")
                    .build()
            );
            var question = questionHelper.insertQuestion(new TestQuestionBuilder()
                    .withTag(List.of("java", "docker"))
                    .withTitle("Some title for the question12345")
                    .withDescription("Some description for the question12345")
                    .withCreatedUserId(user.getId())
                    .build()
            );
            var answer = answerHelper.insertAnswer(new TestAnswerBuilder()
                    .withQuestionId(question.getId())
                    .withUserId(user.getId())
                    .withRespond("Some respond on the question")
                    .build()
            );
            var updateAnswerDto = UpdateAnswerDto.builder()
                    .respond("Some respond on the question1234")
                    .build();

            // When
            addAuthorizationHeader(user2);
            var response = restTemplate.exchange(UPDATE_ANSWER_ENDPOINT, HttpMethod.PUT, new HttpEntity<>(updateAnswerDto), AnswerDto.class, answer.getId());

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        @Test
        @DisplayName("returns status FORBIDDEN if answer tries to update by moderator")
        void updateAnswerShouldReturnForbiddenStatusIfModeratorTriesUpdate() {
            // Given
            var user = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("user")
                    .withEmail("user@gmail.com")
                    .build()
            );
            var moderator = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("moderator")
                    .withEmail("moderator@gmail.com")
                    .build()
            );
            var question = questionHelper.insertQuestion(new TestQuestionBuilder()
                    .withTag(List.of("java", "docker"))
                    .withTitle("Some title for the question12345")
                    .withDescription("Some description for the question12345")
                    .withCreatedUserId(user.getId())
                    .build()
            );
            var answer = answerHelper.insertAnswer(new TestAnswerBuilder()
                    .withQuestionId(question.getId())
                    .withUserId(user.getId())
                    .withRespond("Some respond on the question")
                    .build()
            );
            var updateAnswerDto = UpdateAnswerDto.builder()
                    .respond("Some respond on the question1234")
                    .build();

            // When
            addAuthorizationHeader(moderator);
            var response = restTemplate.exchange(UPDATE_ANSWER_ENDPOINT, HttpMethod.PUT, new HttpEntity<>(updateAnswerDto), AnswerDto.class, answer.getId());

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }
    }

    @DisplayName("PUT request to \"" + MODERATE_ANSWER_ENDPOINT + "\"")
    @Nested
    class ModerateAnswerTest {

        @Test
        @DisplayName("returns status OK if answer moderates by the moderator")
        void moderateAnswerShouldReturnOkStatus() {
            // Given
            var user = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("user")
                    .withEmail("user@gmail.com")
                    .build()
            );
            var moderator = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("moderator")
                    .withEmail("moderator@gmail.com")
                    .withRole(Role.MODERATOR)
                    .build()
            );
            var question = questionHelper.insertQuestion(new TestQuestionBuilder()
                    .withTag(List.of("java", "docker"))
                    .withTitle("Some title for the question12345")
                    .withDescription("Some description for the question12345")
                    .withCreatedUserId(user.getId())
                    .build()
            );
            var answer = answerHelper.insertAnswer(new TestAnswerBuilder()
                    .withQuestionId(question.getId())
                    .withUserId(user.getId())
                    .withRespond("Some respond on the question")
                    .build()
            );
            var moderateAnswerDto = ModerateAnswerDto.builder()
                    .respond("Some respond on the question1234")
                    .hidden(true)
                    .build();

            // When
            addAuthorizationHeader(moderator);
            var response = restTemplate.exchange(MODERATE_ANSWER_ENDPOINT, HttpMethod.PUT, new HttpEntity<>(moderateAnswerDto), AnswerDto.class, answer.getId());

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertThat(response.getBody()).isNotNull();

            var body = response.getBody();
            assertThat(body.getId()).isEqualTo(answer.getId());
            assertThat(body.getUserId()).isEqualTo(user.getId());
            assertThat(body.getQuestionId()).isEqualTo(question.getId());
            assertThat(body.getRespond()).isEqualTo(moderateAnswerDto.getRespond());
            assertThat(body.getTimeModify()).isNotNull();
            assertThat(body.getTimeCreated()).isNotNull();
            assertThat(body.isHidden()).isTrue();
        }

        @Test
        @DisplayName("returns status FORBIDDEN if user tries to moderate another answer")
        void moderateAnswerShouldReturnForbiddenStatusIfAnotherUserTriesModerate() {
            var user = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("user")
                    .withEmail("user@gmail.com")
                    .build()
            );
            var user2 = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("user2")
                    .withEmail("user2@gmail.com")
                    .build()
            );
            var question = questionHelper.insertQuestion(new TestQuestionBuilder()
                    .withTag(List.of("java", "docker"))
                    .withTitle("Some title for the question12345")
                    .withDescription("Some description for the question12345")
                    .withCreatedUserId(user.getId())
                    .build()
            );
            var answer = answerHelper.insertAnswer(new TestAnswerBuilder()
                    .withQuestionId(question.getId())
                    .withUserId(user.getId())
                    .withRespond("Some respond on the question")
                    .build()
            );
            var moderateAnswerDto = ModerateAnswerDto.builder()
                    .respond("Some respond on the question1234")
                    .hidden(true)
                    .build();

            // When
            addAuthorizationHeader(user2);
            var response = restTemplate.exchange(MODERATE_ANSWER_ENDPOINT, HttpMethod.PUT, new HttpEntity<>(moderateAnswerDto), AnswerDto.class, answer.getId());

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }
    }


    @DisplayName("DELETE request to \"" + DELETE_ANSWER_ENDPOINT + "\"")
    @Nested
    class DeleteAnswerTest {

        @Test
        @DisplayName("returns status NO_CONTENT if user deletes own answer")
        void deleteAnswerShouldReturnNoContentStatus() {
            var user = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("user")
                    .withEmail("user@gmail.com")
                    .build()
            );
            var question = questionHelper.insertQuestion(new TestQuestionBuilder()
                    .withTag(List.of("java", "docker"))
                    .withTitle("Some title for the question12345")
                    .withDescription("Some description for the question12345")
                    .withCreatedUserId(user.getId())
                    .build()
            );
            var answer = answerHelper.insertAnswer(new TestAnswerBuilder()
                    .withQuestionId(question.getId())
                    .withUserId(user.getId())
                    .withRespond("Some respond on the question")
                    .build()
            );

            // When
            addAuthorizationHeader(user);
            var response = restTemplate.exchange(DELETE_ANSWER_ENDPOINT, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, answer.getId());

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        @DisplayName("returns status FORBIDDEN if user tries to delete answer")
        void deleteAnswerShouldReturnForbiddenStatusIfAnotherUserTriesUpdate() {
            // Given
            var user = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("user")
                    .withEmail("user@gmail.com")
                    .build()
            );
            var user2 = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("user2")
                    .withEmail("user2@gmail.com")
                    .build()
            );
            var question = questionHelper.insertQuestion(new TestQuestionBuilder()
                    .withTag(List.of("java", "docker"))
                    .withTitle("Some title for the question12345")
                    .withDescription("Some description for the question12345")
                    .withCreatedUserId(user.getId())
                    .build()
            );
            var answer = answerHelper.insertAnswer(new TestAnswerBuilder()
                    .withQuestionId(question.getId())
                    .withUserId(user.getId())
                    .withRespond("Some respond on the question")
                    .build()
            );

            // When
            addAuthorizationHeader(user2);
            var response = restTemplate.exchange(DELETE_ANSWER_ENDPOINT, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, answer.getId());

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        @Test
        @DisplayName("returns status FORBIDDEN if moderator tries to delete answer")
        void deleteAnswerShouldReturnForbiddenStatusIfModeratorTriesUpdate() {
            // Given
            var user = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("user")
                    .withEmail("user@gmail.com")
                    .build()
            );
            var moderator = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("moderator")
                    .withEmail("moderator@gmail.com")
                    .withRole(Role.MODERATOR)
                    .build()
            );
            var question = questionHelper.insertQuestion(new TestQuestionBuilder()
                    .withTag(List.of("java", "docker"))
                    .withTitle("Some title for the question12345")
                    .withDescription("Some description for the question12345")
                    .withCreatedUserId(user.getId())
                    .build()
            );
            var answer = answerHelper.insertAnswer(new TestAnswerBuilder()
                    .withQuestionId(question.getId())
                    .withUserId(user.getId())
                    .withRespond("Some respond on the question")
                    .build()
            );

            // When
            addAuthorizationHeader(moderator);
            var response = restTemplate.exchange(DELETE_ANSWER_ENDPOINT, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, answer.getId());

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }
    }
}
