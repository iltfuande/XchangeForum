package com.terokhin.graduate.config;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties("graduate")
public class MainConfiguration {

    @NotNull
    private String jwtSecret;
    
    private long jwtAccessTokenExpirationMs;
}
