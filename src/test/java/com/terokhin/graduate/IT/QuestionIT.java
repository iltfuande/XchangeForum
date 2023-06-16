package com.terokhin.graduate.IT;

import com.terokhin.graduate.helper.builder.TestQuestionBuilder;
import com.terokhin.graduate.helper.builder.TestUserBuilder;
import com.terokhin.graduate.model.dto.question.CreateQuestionDto;
import com.terokhin.graduate.model.dto.question.ModerateQuestionDto;
import com.terokhin.graduate.model.dto.question.QuestionDto;
import com.terokhin.graduate.model.dto.question.UpdateQuestionDto;
import com.terokhin.graduate.security.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuestionIT extends BaseIT {

    private static final String GET_QUESTION_ENDPOINT = "/api/v1/question/{questionId}";
    private static final String GET_ALL_QUESTION_ENDPOINT = "/api/v1/question";
    private static final String CREATE_QUESTION_ENDPOINT = "/api/v1/question";
    private static final String UPDATE_QUESTION_ENDPOINT = "/api/v1/question/{questionId}";
    private static final String MODERATE_QUESTION_ENDPOINT = "/api/v1/question/{questionId}/moderate";
    private static final String DELETE_QUESTION_ENDPOINT = "/api/v1/question/{questionId}";

    @Nested
    @DisplayName("GET request to \"" + GET_QUESTION_ENDPOINT + "\"")
    class GetQuestionTest {

        @Test
        @DisplayName("returns question")
        void getQuestionShouldReturnOkStatus() {
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

            // When
            var response = restTemplate.exchange(GET_QUESTION_ENDPOINT, HttpMethod.GET, HttpEntity.EMPTY, QuestionDto.class, question.getId());

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertThat(response.getBody()).isNotNull();

            var body = response.getBody();
            assertThat(body.getId()).isEqualTo(question.getId());
            assertThat(body.getCreatedUserId()).isEqualTo(question.getCreatedUserId());
            assertThat(body.getDescription()).isEqualTo(question.getDescription());
            assertThat(body.getTitle()).isEqualTo(question.getTitle());
            assertThat(body.getTimeModify()).isNull();
            assertThat(body.getTimeCreated()).isNotNull();
            assertThat(body.getViewed()).isEqualTo(question.getViewed());
            assertThat(body.getTag()).isEqualTo(question.getTag());
        }

        @Test
        @DisplayName("returns status BAD_REQUEST if questionId does not exist")
        void getQuestionShouldReturnBadRequestStatusIfQuestionIdDoesNotExist() {
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
            long nonExistingQuestionId = question.getId() + 1L;

            // When
            addAuthorizationHeader(user);
            var response = restTemplate.exchange(GET_QUESTION_ENDPOINT, HttpMethod.GET, HttpEntity.EMPTY, Void.class, nonExistingQuestionId);

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @DisplayName("POST request to \"" + CREATE_QUESTION_ENDPOINT + "\"")
    @Nested
    class CreateQuestionTest {

        @Test
        @DisplayName("returns status OK and create question")
        void createQuestionShouldReturnOkStatus() {
            // Given
            var user = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("user")
                    .withEmail("user@gmail.com")
                    .build()
            );
            var createQuestionDto = CreateQuestionDto.builder()
                    .title("This is my title")
                    .description("This is the my description for the my question")
                    .tag(List.of("java", "docker"))
                    .build();

            // When
            addAuthorizationHeader(user);
            var response = restTemplate.exchange(CREATE_QUESTION_ENDPOINT, HttpMethod.POST, new HttpEntity<>(createQuestionDto), QuestionDto.class);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();

            var body = response.getBody();
            assertThat(body.getId()).isNotNull();
            assertThat(body.getTitle()).isEqualTo(createQuestionDto.getTitle());
            assertThat(body.getDescription()).isEqualTo(createQuestionDto.getDescription());
            assertThat(body.getTag()).isEqualTo(createQuestionDto.getTag());
            assertThat(body.getCreatedUserId()).isEqualTo(user.getId());
            assertThat(body.getViewed()).isZero();
            assertThat(body.getTimeCreated()).isNotNull();
            assertThat(body.getTimeModify()).isNull();
        }
    }

//    @Nested
//    @DisplayName("GET request to \"" + GET_ALL_QUESTION_ENDPOINT + "\"")
//    class GetAllQuestionTest {
//
//        @Test
//        @DisplayName("returns question")
//        void getAllQuestionShouldReturnOkStatus() {
//            // Given
//            var user = userHelper.insertUser(new TestUserBuilder()
//                    .withNickname("user")
//                    .withEmail("user@gmail.com")
//                    .build()
//            );
//            var question1 = questionHelper.insertQuestion(new TestQuestionBuilder()
//                    .withTag(List.of("java", "docker"))
//                    .withTitle("Some title for the question12345")
//                    .withDescription("Some description for the question12345")
//                    .withCreatedUserId(user.getId())
//                    .build()
//            );
//            var question2 = questionHelper.insertQuestion(new TestQuestionBuilder()
//                    .withTag(List.of("java", "docker-compose"))
//                    .withTitle("Some title for the question2345")
//                    .withDescription("Some description for the question1345")
//                    .withCreatedUserId(user.getId())
//                    .build()
//            );
//            var question3 = questionHelper.insertQuestion(new TestQuestionBuilder()
//                    .withTag(List.of("angular", "docker"))
//                    .withTitle("Some title for the question?")
//                    .withDescription("Some description for the question1")
//                    .withCreatedUserId(user.getId())
//                    .build()
//            );
//
//            // When
//            var response = restTemplate.exchange(GET_ALL_QUESTION_ENDPOINT, HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<Page<QuestionDto>>() {});
//
//            // Then
//            assertEquals(HttpStatus.OK, response.getStatusCode());
//            assertThat(response.getBody()).isNotNull();
//
//            var body = response.getBody().getContent();
//            
//            var responseQuestion1 = body.get(0);
//            assertThat(responseQuestion1.getId()).isEqualTo(question1.getId());
//            assertThat(responseQuestion1.getCreatedUserId()).isEqualTo(question1.getCreatedUserId());
//            assertThat(responseQuestion1.getDescription()).isEqualTo(question1.getDescription());
//            assertThat(responseQuestion1.getTitle()).isEqualTo(question1.getTitle());
//            assertThat(responseQuestion1.getTimeModify()).isNull();
//            assertThat(responseQuestion1.getTimeCreated()).isNotNull();
//            assertThat(responseQuestion1.getViewed()).isEqualTo(question1.getViewed());
//            assertThat(responseQuestion1.getTag()).isEqualTo(question1.getTag());
//            
//            var responseQuestion2 = body.get(1);
//            assertThat(responseQuestion2.getId()).isEqualTo(question2.getId());
//            assertThat(responseQuestion2.getCreatedUserId()).isEqualTo(question2.getCreatedUserId());
//            assertThat(responseQuestion2.getDescription()).isEqualTo(question2.getDescription());
//            assertThat(responseQuestion2.getTitle()).isEqualTo(question2.getTitle());
//            assertThat(responseQuestion2.getTimeModify()).isNull();
//            assertThat(responseQuestion2.getTimeCreated()).isNotNull();
//            assertThat(responseQuestion2.getViewed()).isEqualTo(question2.getViewed());
//            assertThat(responseQuestion2.getTag()).isEqualTo(question2.getTag());
//            
//            var responseQuestion3 = body.get(2);
//            assertThat(responseQuestion3.getId()).isEqualTo(question3.getId());
//            assertThat(responseQuestion3.getCreatedUserId()).isEqualTo(question3.getCreatedUserId());
//            assertThat(responseQuestion3.getDescription()).isEqualTo(question3.getDescription());
//            assertThat(responseQuestion3.getTitle()).isEqualTo(question3.getTitle());
//            assertThat(responseQuestion3.getTimeModify()).isNull();
//            assertThat(responseQuestion3.getTimeCreated()).isNotNull();
//            assertThat(responseQuestion3.getViewed()).isEqualTo(question3.getViewed());
//            assertThat(responseQuestion3.getTag()).isEqualTo(question3.getTag());
//        }
//    }

    @DisplayName("PUT request to \"" + UPDATE_QUESTION_ENDPOINT + "\"")
    @Nested
    class UpdateQuestionTest {

        @Test
        @DisplayName("returns status OK if question update by the user")
        void updateQuestionShouldReturnOkStatus() {
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
            var updateQuestionDto = UpdateQuestionDto.builder()
                    .title("This is my title?")
                    .description("This is the my description for the my question.")
                    .tag(List.of("java", "docker", "angular"))
                    .build();

            // When
            addAuthorizationHeader(user);
            var response = restTemplate.exchange(UPDATE_QUESTION_ENDPOINT, HttpMethod.PUT, new HttpEntity<>(updateQuestionDto), QuestionDto.class, question.getId());

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();

            var body = response.getBody();
            assertThat(body.getId()).isEqualTo(question.getId());
            assertThat(body.getTitle()).isEqualTo(updateQuestionDto.getTitle());
            assertThat(body.getDescription()).isEqualTo(updateQuestionDto.getDescription());
            assertThat(body.getTag()).isEqualTo(updateQuestionDto.getTag());
            assertThat(body.getCreatedUserId()).isEqualTo(user.getId());
            assertThat(body.getViewed()).isZero();
            assertThat(body.isHidden()).isFalse();
            assertThat(body.isClose()).isFalse();
            assertThat(body.getTimeCreated()).isNotNull();
            assertThat(body.getTimeModify()).isNotNull();
        }

        @Test
        @DisplayName("returns status FORBIDDEN if question tries to update by another user")
        void updateQuestionShouldReturnForbiddenStatusIfAnotherUserTriesUpdate() {
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
            var updateQuestionDto = UpdateQuestionDto.builder()
                    .title("This is my title?")
                    .description("This is the my description for the my question.")
                    .tag(List.of("java", "docker", "angular"))
                    .build();

            // When
            addAuthorizationHeader(user2);
            var response = restTemplate.exchange(UPDATE_QUESTION_ENDPOINT, HttpMethod.PUT, new HttpEntity<>(updateQuestionDto), QuestionDto.class, question.getId());

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        @Test
        @DisplayName("returns status FORBIDDEN if user tries to update by moderator")
        void updateQuestionShouldReturnForbiddenStatusIfModeratorTriesUpdate() {
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
            var updateQuestionDto = UpdateQuestionDto.builder()
                    .title("This is my title?")
                    .description("This is the my description for the my question.")
                    .tag(List.of("java", "docker", "angular"))
                    .build();

            // When
            addAuthorizationHeader(moderator);
            var response = restTemplate.exchange(UPDATE_QUESTION_ENDPOINT, HttpMethod.PUT, new HttpEntity<>(updateQuestionDto), QuestionDto.class, question.getId());

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }
    }

    @DisplayName("PUT request to \"" + MODERATE_QUESTION_ENDPOINT + "\"")
    @Nested
    class ModerateQuestionTest {

        @Test
        @DisplayName("returns status OK if question moderates by the moderator")
        void moderateQuestionShouldReturnOkStatus() {
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
            var moderateQuestionDto = ModerateQuestionDto.builder()
                    .title("This is my title?")
                    .description("This is the my description for the my question.")
                    .tag(List.of("java", "docker", "angular"))
                    .close(true)
                    .hidden(true)
                    .build();

            // When
            addAuthorizationHeader(moderator);
            var response = restTemplate.exchange(MODERATE_QUESTION_ENDPOINT, HttpMethod.PUT, new HttpEntity<>(moderateQuestionDto), QuestionDto.class, question.getId());

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();

            var body = response.getBody();
            assertThat(body.getId()).isEqualTo(question.getId());
            assertThat(body.getTitle()).isEqualTo(moderateQuestionDto.getTitle());
            assertThat(body.getDescription()).isEqualTo(moderateQuestionDto.getDescription());
            assertThat(body.getTag()).isEqualTo(moderateQuestionDto.getTag());
            assertThat(body.getCreatedUserId()).isEqualTo(user.getId());
            assertThat(body.getViewed()).isZero();
            assertThat(body.isHidden()).isTrue();
            assertThat(body.isClose()).isTrue();
            assertThat(body.getTimeCreated()).isNotNull();
            assertThat(body.getTimeModify()).isNotNull();
        }

        @Test
        @DisplayName("returns status FORBIDDEN if user tries to moderate another question")
        void moderateQuestionShouldReturnForbiddenStatusIfAnotherUserTriesModerate() {
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
            var moderateQuestionDto = ModerateQuestionDto.builder()
                    .title("This is my title?")
                    .description("This is the my description for the my question.")
                    .tag(List.of("java", "docker", "angular"))
                    .close(true)
                    .hidden(true)
                    .build();

            // When
            addAuthorizationHeader(user2);
            var response = restTemplate.exchange(MODERATE_QUESTION_ENDPOINT, HttpMethod.PUT, new HttpEntity<>(moderateQuestionDto), QuestionDto.class, question.getId());

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }
    }


    @DisplayName("DELETE request to \"" + DELETE_QUESTION_ENDPOINT + "\"")
    @Nested
    class DeleteQuestionTest {

        @Test
        @DisplayName("returns status NO_CONTENT if user deletes own question")
        void deleteQuestionShouldReturnNoContentStatus() {
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

            // When
            addAuthorizationHeader(user);
            var response = restTemplate.exchange(DELETE_QUESTION_ENDPOINT, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, question.getId());

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        @DisplayName("returns status FORBIDDEN if user tries to delete question")
        void deleteQuestionShouldReturnForbiddenStatusIfAnotherUserTriesUpdate() {
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

            // When
            addAuthorizationHeader(user2);
            var response = restTemplate.exchange(DELETE_QUESTION_ENDPOINT, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, question.getId());

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        @Test
        @DisplayName("returns status FORBIDDEN if moderator tries to delete question")
        void deleteQuestionShouldReturnForbiddenStatusIfModeratorTriesUpdate() {
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

            // When
            addAuthorizationHeader(moderator);
            var response = restTemplate.exchange(DELETE_QUESTION_ENDPOINT, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, question.getId());

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }
    }
}
