package com.app.util;

import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HerokuRestarter {
	private static final String HEROKU_API_KEY = System.getenv("HEROKU_API_KEY");
    private static final String APP_NAME = "jcheduler-job";
    private static final WebClient webClient = WebClient.builder().baseUrl("https://api.heroku.com").build();

    public static void restartHerokuDyno() {
        log.info("Heroku Dyno 재시작 요청...");

        try {
            webClient.delete()
                .uri("/apps/{appName}/dynos", APP_NAME)
                .header("Authorization", "Bearer " + HEROKU_API_KEY)
                .header("Accept", "application/vnd.heroku+json; version=3")
                .retrieve()
                .bodyToMono(Void.class)
                .block();

            log.info("Heroku Dyno 재시작 성공!");
        } catch (Exception e) {
            log.error("Heroku Dyno 재시작 실패: {}", e.getMessage());
        }
    }

}
