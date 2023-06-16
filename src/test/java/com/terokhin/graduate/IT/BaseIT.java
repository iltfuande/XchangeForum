package com.terokhin.graduate.IT;

import com.terokhin.graduate.helper.AccessHelper;
import com.terokhin.graduate.helper.AnswerHelper;
import com.terokhin.graduate.helper.QuestionHelper;
import com.terokhin.graduate.helper.UserHelper;
import com.terokhin.graduate.model.enity.User;
import com.terokhin.graduate.repository.UserRepository;
import com.terokhin.graduate.security.JwtService;
import com.terokhin.graduate.service.AnswerService;
import com.terokhin.graduate.service.QuestionService;
import com.terokhin.graduate.service.UserService;
import com.terokhin.graduate.util.AccessService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;

@Testcontainers
@TestConfiguration
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BaseIT {

    public static final String[] TABLES = {
            "question_tag",
            "answer",
            "question",
            "\"user\"",
    };

    @Value("${graduate.jwtSecret}")
    private String secret;

    @Autowired
    protected ModelMapper modelMapper;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JwtService jwtService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected QuestionService questionService;

    @Autowired
    protected AnswerService answerService;

    @Autowired
    protected UserHelper userHelper;

    @Autowired
    protected AnswerHelper answerHelper;

    @Autowired
    protected QuestionHelper questionHelper;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected AccessService accessService;

    @AfterEach
    void tearDown() {
//        AccessHelper.clearAuthentication();
        cleanAuthorizationHeaderToken();
        JdbcTestUtils.deleteFromTables(jdbcTemplate, TABLES);
    }

    protected void addAuthorizationHeader(User user) {
        String token = jwtService.generateToken(user);
        addAuthorizationHeaderToken(token);
        AccessHelper.setCurrentUser(user.getUsername());
    }

    protected void addAuthorizationHeaderToken(String token) {
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((req, body, execution) -> {
                    req.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                    return execution.execute(req, body);
                }));
    }
    
    protected void cleanAuthorizationHeaderToken() {
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((req, body, execution) -> {
                    req.getHeaders().remove(HttpHeaders.AUTHORIZATION);
                    return execution.execute(req, body);
                }));
    }
}