package com.app.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.app.common.constants.RestApiProperties;

@Configuration
@EnableConfigurationProperties(RestApiProperties.class)
public class PropertiesConfig {

}
