package com.app.common.constants;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rest-api")
public record RestApiProperties(
    Heroku heroku, 
    Guney guney, 
    Batch batch
) {
    public record Heroku(String baseUrl) {}
    public record Guney(String baseUrl) {}
    public record Batch(String baseUrl) {}
}