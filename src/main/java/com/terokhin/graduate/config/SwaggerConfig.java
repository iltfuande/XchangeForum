package com.terokhin.graduate.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Graduate Application"),
        servers = {@Server(url = "/")}
)
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer",
        description = "You need [to get the bearer token](index.html#/authentication-controller). " +
                      "After that take accessToken from response body and paste it into \"Value\" (without \"bearer\").")
public class SwaggerConfig {

}
