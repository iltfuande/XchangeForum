package com.terokhin.graduate.IT;

import com.terokhin.graduate.helper.builder.TestUserBuilder;
import com.terokhin.graduate.model.dto.user.CreateUserDto;
import com.terokhin.graduate.model.dto.user.auth.AuthenticationRequest;
import com.terokhin.graduate.model.dto.user.auth.AuthenticationResponse;
import com.terokhin.graduate.security.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthenticationIT extends BaseIT {

    private static final String REGISTER_USER_ENDPOINT = "/api/v1/auth/register";
    private static final String AUTH_USER_ENDPOINT = "/api/v1/auth/authenticate";

    @Nested
    @DisplayName("POST request to \"" + REGISTER_USER_ENDPOINT + "\"")
    class RegisterTest {

        @Test
        @DisplayName("returns user token and created user")
        void registerUser() {
            // Given
            var createdUserDto = CreateUserDto.builder()
                    .firstName("Joe")
                    .lastName("Doce")
                    .email("joe@gmail.com")
                    .nickname("jdoke")
                    .password("12345")
                    .build();

            // When
            var response = restTemplate.exchange(REGISTER_USER_ENDPOINT, HttpMethod.POST, new HttpEntity<>(createdUserDto), AuthenticationResponse.class);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertThat(response.getBody()).isNotNull();

            var body = response.getBody();
            assertThat(body.getToken()).isNotNull();
            assertThat(body.getTokenTimeExpiration()).isAfter(Instant.now());
            assertThat(body.getRole()).isEqualTo(Role.USER);
            assertThat(body.getUserDto()).isNotNull();

            var user = body.getUserDto();
            assertThat(user.getEmail()).isEqualTo(createdUserDto.getEmail());
            assertThat(user.getNickname()).isEqualTo(createdUserDto.getNickname());
            assertThat(user.getFirstName()).isEqualTo(createdUserDto.getFirstName());
            assertThat(user.getLastName()).isEqualTo(createdUserDto.getLastName());
            assertThat(user.isBanned()).isFalse();
            assertThat(user.getTimeCreated()).isNotNull();
            assertTrue(passwordEncoder.matches(createdUserDto.getPassword(), user.getPassword()));
        }
        
        @Test
        @DisplayName("returns status BAD_REQUEST if email already register")
        void registerUserIfEmailAlreadyRegister() {
            // Given
            var user = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("user")
                    .withEmail("joe@gmail.com")
                    .build()
            );
            var createdUserDto = CreateUserDto.builder()
                    .firstName("Joe")
                    .lastName("Doce")
                    .email("joe@gmail.com")
                    .nickname("jdoke")
                    .password("12345")
                    .build();

            // When
            var response = restTemplate.exchange(REGISTER_USER_ENDPOINT, HttpMethod.POST, new HttpEntity<>(createdUserDto), String.class);

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
        
        @Test
        @DisplayName("returns status BAD_REQUEST if nickname already register")
        void registerUserIfNicknameAlreadyRegister() {
            // Given
            var user = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("jdoke")
                    .withEmail("user@gmail.com")
                    .build()
            );
            var createdUserDto = CreateUserDto.builder()
                    .firstName("Joe")
                    .lastName("Doce")
                    .email("joe@gmail.com")
                    .nickname("jdoke")
                    .password("12345")
                    .build();

            // When
            var response = restTemplate.exchange(REGISTER_USER_ENDPOINT, HttpMethod.POST, new HttpEntity<>(createdUserDto), String.class);

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @DisplayName("PUT request to \"" + AUTH_USER_ENDPOINT + "\"")
    @Nested
    class AuthTest {

        @Test
        @DisplayName("returns status OK and token")
        void authTest() {
            // Given
            var user = userHelper.insertUser(new TestUserBuilder()
                    .withNickname("user")
                    .withEmail("user@gmail.com")
                    .withPassword(passwordEncoder.encode("12345"))
                    .build()
            );
            var authenticationRequest = AuthenticationRequest.builder()
                    .nickname("user")
                    .password("12345")
                    .build();

            // When
            var response = restTemplate.exchange(AUTH_USER_ENDPOINT, HttpMethod.POST, new HttpEntity<>(authenticationRequest), AuthenticationResponse.class);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertThat(response.getBody()).isNotNull();

            var body = response.getBody();
            assertThat(body.getToken()).isNotNull();
            assertThat(body.getTokenTimeExpiration()).isAfter(Instant.now());
            assertThat(body.getRole()).isEqualTo(Role.USER);
            assertThat(body.getUserDto()).isNotNull();

            var userFromBody = body.getUserDto();
            assertThat(userFromBody.getEmail()).isEqualTo(user.getEmail());
            assertThat(userFromBody.getNickname()).isEqualTo(user.getNickname());
            assertThat(userFromBody.getFirstName()).isEqualTo(user.getFirstName());
            assertThat(userFromBody.getLastName()).isEqualTo(user.getLastName());
            assertThat(userFromBody.isBanned()).isFalse();
            assertThat(userFromBody.getTimeCreated()).isNotNull();
            assertTrue(passwordEncoder.matches(authenticationRequest.getPassword(), userFromBody.getPassword()));
        }
    }
}
