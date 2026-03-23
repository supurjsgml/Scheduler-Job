package com.app.common.constants;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rest-api")
public record RestApiProperties(
    Heroku heroku, 
    Kakao kakao, 
    Guney guney, 
    Batch batch
) {
    public record Heroku(String baseUrl) {}
    public record Guney(String baseUrl) {}
    public record Batch(String baseUrl) {}

    public record Kakao(
        String v2,    // YAML의 v2:
        Api api,      // YAML의 api:
        Auth auth     // YAML의 auth:
    ) {
        public record Api(String baseUrl, String memo) {}
        public record Auth(String baseUrl, String token) {}
    }
}