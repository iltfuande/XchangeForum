package com.terokhin.graduate.IT;

import com.terokhin.graduate.helper.builder.TestUserBuilder;
import com.terokhin.graduate.model.dto.user.ModerateUserDto;
import com.terokhin.graduate.model.dto.user.UpdateUserDto;
import com.terokhin.graduate.model.dto.user.UserDto;
import com.terokhin.graduate.security.Role;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserIT extends BaseIT {

    private static final String GET_USER_ENDPOINT = "/api/v1/user/{userId}";
    private static final String UPDATE_USER_ENDPOINT = "/api/v1/user/{userId}";
    private static final String MODERATE_USER_ENDPOINT = "/api/v1/user/{userId}/moderate";
    private static final String DELETE_USER_ENDPOINT = "/api/v1/user/{userId}";

    @Nested
    @DisplayName("GET request to \"" + GET_USER_ENDPOINT + "\"")
    class GetUserTest {

        @Test
        @DisplayName("returns user without sensitive data if user is a owner")
        void getUserShouldReturnOkStatusAndUserDtoIfUserIdExists() {
            // Given
            var user = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("user")
                    .withEmail("user@gmail.com")
                    .build()
            );

            // When
            addAuthorizationHeader(user);
            var response = restTemplate.exchange(GET_USER_ENDPOINT, HttpMethod.GET, HttpEntity.EMPTY, UserDto.class, user.getId());

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertThat(response.getBody()).isNotNull();

            var body = response.getBody();
            assertThat(body.getId()).isEqualTo(user.getId());
            assertThat(body.getEmail()).isEqualTo(user.getEmail());
            assertThat(body.getPassword()).isEqualTo(user.getPassword());
            assertThat(body.getNickname()).isEqualTo(user.getNickname());
            assertThat(body.getFirstName()).isEqualTo(user.getFirstName());
            assertThat(body.getLastName()).isEqualTo(user.getLastName());
            assertThat(body.isBanned()).isEqualTo(user.isBanned());
            assertThat(body.getTimeCreated()).isNotNull();
        }

        @Test
        @DisplayName("returns user without sensitive data if user is a moderator")
        void getUserReturnsUserWithoutSensitiveDataForModerator() {
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

            // When
            addAuthorizationHeader(moderator);
            var response = restTemplate.exchange(GET_USER_ENDPOINT, HttpMethod.GET, HttpEntity.EMPTY, UserDto.class, user.getId());

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertThat(response.getBody()).isNotNull();

            var body = response.getBody();
            assertThat(body.getId()).isEqualTo(user.getId());
            assertThat(body.getEmail()).isEqualTo(user.getEmail());
            assertThat(body.getPassword()).isEqualTo(user.getPassword());
            assertThat(body.getNickname()).isEqualTo(user.getNickname());
            assertThat(body.getFirstName()).isEqualTo(user.getFirstName());
            assertThat(body.getLastName()).isEqualTo(user.getLastName());
            assertThat(body.isBanned()).isEqualTo(user.isBanned());
            assertThat(body.getTimeCreated()).isNotNull();
        }

        @Test
        @DisplayName("returns user with sensitive data if user is not the owner and not a moderator")
        void getUserReturnsUserWithSensitiveDataForNonOwnerNonModerator() {
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

            // When
            addAuthorizationHeader(user2);
            var response = restTemplate.exchange(GET_USER_ENDPOINT, HttpMethod.GET, HttpEntity.EMPTY, UserDto.class, user.getId());

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertThat(response.getBody()).isNotNull();

            var body = response.getBody();
            assertThat(body.getId()).isEqualTo(user.getId());
            assertThat(body.getEmail()).isNull();
            assertThat(body.getPassword()).isNull();
            assertThat(body.getNickname()).isEqualTo(user.getNickname());
            assertThat(body.getFirstName()).isEqualTo(user.getFirstName());
            assertThat(body.getLastName()).isEqualTo(user.getLastName());
            assertThat(body.isBanned()).isEqualTo(user.isBanned());
            assertThat(body.getTimeCreated()).isNotNull();
        }       
        
        @Test
        @DisplayName("returns user with sensitive data for viewer")
        void getUserReturnsUserWithSensitiveDataForViewer() {
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

            // When
            var response = restTemplate.exchange(GET_USER_ENDPOINT, HttpMethod.GET, HttpEntity.EMPTY, UserDto.class, user.getId());

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertThat(response.getBody()).isNotNull();

            var body = response.getBody();
            assertThat(body.getId()).isEqualTo(user.getId());
            assertThat(body.getEmail()).isNull();
            assertThat(body.getPassword()).isNull();
            assertThat(body.getNickname()).isEqualTo(user.getNickname());
            assertThat(body.getFirstName()).isEqualTo(user.getFirstName());
            assertThat(body.getLastName()).isEqualTo(user.getLastName());
            assertThat(body.isBanned()).isEqualTo(user.isBanned());
            assertThat(body.getTimeCreated()).isNotNull();
        }

        @Test
        @DisplayName("returns status BAD_REQUEST if userId does not exist")
        void getUserShouldReturnBadRequestStatusIfUserIdDoesNotExist() {
            // Given
            var user = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("user")
                    .withEmail("user@gmail.com")
                    .build()
            );
            long nonExistingUserId = user.getId() + 1L;

            // When
            addAuthorizationHeader(user);
            var response = restTemplate.exchange(GET_USER_ENDPOINT, HttpMethod.GET, HttpEntity.EMPTY, Void.class, nonExistingUserId);

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @DisplayName("PUT request to \"" + UPDATE_USER_ENDPOINT + "\"")
    @Nested
    class UpdateUserTest {

        @Test
        @DisplayName("returns status OK if user updates own profile")
        void updateUserShouldReturnOkStatus() {
            // Given
            var user = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("user")
                    .withEmail("user@gmail.com")
                    .build()
            );
            var updateUserDto = UpdateUserDto.builder()
                    .firstName("Misha")
                    .lastName("Terokhin")
                    .password("12345")
                    .build();

            // When
            addAuthorizationHeader(user);
            var response = restTemplate.exchange(UPDATE_USER_ENDPOINT, HttpMethod.PUT, new HttpEntity<>(updateUserDto), UserDto.class, user.getId());

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();

            var body = response.getBody();
            assertThat(body.getId()).isEqualTo(user.getId());
            assertThat(body.getEmail()).isEqualTo(user.getEmail());
            assertThat(body.getNickname()).isEqualTo(user.getNickname());
            assertThat(body.getFirstName()).isEqualTo(updateUserDto.getFirstName());
            assertThat(body.getLastName()).isEqualTo(updateUserDto.getLastName());
            assertThat(body.isBanned()).isEqualTo(user.isBanned());
            assertThat(body.getTimeCreated()).isNotNull();
            assertTrue(passwordEncoder.matches(updateUserDto.getPassword(), body.getPassword()));
        }

        @Test
        @DisplayName("returns status FORBIDDEN if user tries to update another user profile")
        void updateUserShouldReturnForbiddenStatusIfAnotherUserTriesUpdate() {
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
            var updateUserDto = UpdateUserDto.builder()
                    .firstName("Misha")
                    .lastName("Terokhin")
                    .password("12345")
                    .build();

            // When
            addAuthorizationHeader(user2);
            var response = restTemplate.exchange(UPDATE_USER_ENDPOINT, HttpMethod.PUT, new HttpEntity<>(updateUserDto), UserDto.class, user.getId());

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        @Test
        @DisplayName("returns status FORBIDDEN if moderator tries to update another user profile")
        void updateUserShouldReturnForbiddenStatusIfModeratorTriesUpdate() {
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
            var updateUserDto = UpdateUserDto.builder()
                    .firstName("Misha")
                    .lastName("Terokhin")
                    .password("12345")
                    .build();

            // When
            addAuthorizationHeader(moderator);
            var response = restTemplate.exchange(UPDATE_USER_ENDPOINT, HttpMethod.PUT, new HttpEntity<>(updateUserDto), UserDto.class, user.getId());

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        @Test
        @DisplayName("returns status FORBIDDEN if user is not authenticated")
        void updateUserShouldReturnForbiddenStatusIfUserNotAuthenticated() {
            // Given
            var user = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("user")
                    .withEmail("user@gmail.com")
                    .build()
            );
            var updateUserDto = UpdateUserDto.builder()
                    .firstName("Misha")
                    .lastName("Terokhin")
                    .password("12345")
                    .build();

            // When
            var response = restTemplate.exchange(UPDATE_USER_ENDPOINT, HttpMethod.PUT, new HttpEntity<>(updateUserDto), UserDto.class, user.getId());

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }
    }

    @DisplayName("PUT request to \"" + MODERATE_USER_ENDPOINT + "\"")
    @Nested
    class ModerateUserTest {

        @Test
        @DisplayName("returns status OK if user moderates the moderator")
        void moderateUserShouldReturnOkStatus() {
            // Given
            var moderator = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("moderator")
                    .withEmail("moderator@gmail.com")
                    .withRole(Role.MODERATOR)
                    .build()
            );
            var user = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("user")
                    .withEmail("user@gmail.com")
                    .build()
            );
            var moderateUserDto = ModerateUserDto.builder()
                    .firstName("Misha")
                    .lastName("Terokhin")
                    .password("12345")
                    .banned(true)
                    .build();

            // When
            addAuthorizationHeader(moderator);
            var response = restTemplate.exchange(MODERATE_USER_ENDPOINT, HttpMethod.PUT, new HttpEntity<>(moderateUserDto), UserDto.class, user.getId());

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();

            var body = response.getBody();
            assertThat(body.getId()).isEqualTo(user.getId());
            assertThat(body.getEmail()).isEqualTo(user.getEmail());
            assertThat(body.getNickname()).isEqualTo(user.getNickname());
            assertThat(body.getFirstName()).isEqualTo(moderateUserDto.getFirstName());
            assertThat(body.getLastName()).isEqualTo(moderateUserDto.getLastName());
            assertThat(body.isBanned()).isEqualTo(moderateUserDto.isBanned());
            assertThat(body.getTimeCreated()).isNotNull();
            assertTrue(passwordEncoder.matches(moderateUserDto.getPassword(), body.getPassword()));
        }

        @Test
        @DisplayName("returns status FORBIDDEN if user tries to moderate another user profile")
        void moderateUserShouldReturnForbiddenStatusIfAnotherUserTriesUpdate() {
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
            var moderateUserDto = ModerateUserDto.builder()
                    .firstName("Misha")
                    .lastName("Terokhin")
                    .password("12345")
                    .banned(true)
                    .build();

            // When
            addAuthorizationHeader(user2);
            var response = restTemplate.exchange(MODERATE_USER_ENDPOINT, HttpMethod.PUT, new HttpEntity<>(moderateUserDto), UserDto.class, user.getId());

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        @Test
        @DisplayName("returns status FORBIDDEN if user is not authenticated")
        void moderateUserShouldReturnForbiddenStatusIfUserNotAuthenticated() {
            // Given
            var user = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("user")
                    .withEmail("user@gmail.com")
                    .build()
            );
            var moderateUserDto = ModerateUserDto.builder()
                    .firstName("Misha")
                    .lastName("Terokhin")
                    .password("12345")
                    .banned(true)
                    .build();

            // When
            var response = restTemplate.exchange(MODERATE_USER_ENDPOINT, HttpMethod.PUT, new HttpEntity<>(moderateUserDto), UserDto.class, user.getId());

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }
    }


    @DisplayName("DELETE request to \"" + DELETE_USER_ENDPOINT + "\"")
    @Nested
    class DeleteUserTest {

        @Test
        @DisplayName("returns status NO_CONTENT if user deletes own profile")
        void deleteUserShouldReturnNoContentStatus() {
            // Given
            var user = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("user")
                    .withEmail("user@gmail.com")
                    .build()
            );

            // When
            addAuthorizationHeader(user);
            var response = restTemplate.exchange(DELETE_USER_ENDPOINT, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, user.getId());

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            
            var userFromRepo = userRepository.findByNickname(user.getNickname()).get();
            assertThat(userFromRepo.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("returns status FORBIDDEN if user tries to delete another user profile")
        void deleteUserShouldReturnForbiddenStatusIfAnotherUserTriesUpdate() {
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

            // When
            addAuthorizationHeader(user2);
            var response = restTemplate.exchange(DELETE_USER_ENDPOINT, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, user.getId());

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        @Test
        @DisplayName("returns status FORBIDDEN if moderator tries to delete another user profile")
        void deleteUserShouldReturnForbiddenStatusIfModeratorTriesUpdate() {
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

            // When
            addAuthorizationHeader(moderator);
            var response = restTemplate.exchange(DELETE_USER_ENDPOINT, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, user.getId());
            
            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }
    }
}
