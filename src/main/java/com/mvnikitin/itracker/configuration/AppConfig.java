package com.mvnikitin.itracker.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("com.mvnikitin.itracker")
@PropertySource("classpath:application.properties")
public class AppConfig {
}
