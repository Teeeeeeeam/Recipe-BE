package com.team.RecipeRadar.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.util.Arrays;

@OpenAPIDefinition(
        info = @Info(title = "나만의 냉장고 API 명세서",
        description = "백엔드 API 서버",
        version = "v1")
)
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI3() {
        String accessTokenKey = "Access Token (Bearer)";
        String refreshTokenKey = "RefreshToken";

        // 보안 요구사항 설정
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(accessTokenKey)
                .addList(refreshTokenKey);

        // Access Token에 대한 SecurityScheme 정의
        SecurityScheme accessTokenSecurityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("Bearer") // 스키마를 Bearer로 설정
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name(HttpHeaders.AUTHORIZATION);

        // Refresh Token에 대한 SecurityScheme 정의
        SecurityScheme refreshTokenSecurityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name(refreshTokenKey);

        // Components 객체에 SecurityScheme 추가
        Components components = new Components()
                .addSecuritySchemes(accessTokenKey, accessTokenSecurityScheme)
                .addSecuritySchemes(refreshTokenKey, refreshTokenSecurityScheme);

        // OpenAPI 객체 생성 및 구성
        return new OpenAPI()
                .addSecurityItem(securityRequirement) // 보안 요구사항 추가
                .components(components);
    }

    @Bean
    public GroupedOpenApi chatOpenApi() {
        String[] paths = {"/api/**"};

        return GroupedOpenApi.builder()
                .group("나만의 냉장고 API v1")
                .pathsToMatch(paths)
                .build();
    }
}
