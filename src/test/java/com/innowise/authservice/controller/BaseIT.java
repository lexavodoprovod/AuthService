package com.innowise.authservice.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Duration;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public abstract class BaseIT {

    static final PostgreSQLContainer<?> postgres;


    static {
        postgres = new PostgreSQLContainer<>("postgres:15-alpine")
                .withDatabaseName("testdb")
                .withUsername("testuser")
                .withPassword("testpass")
                .withStartupTimeout(Duration.ofMinutes(2));

        postgres.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("SECRET_KEY", () -> "R0hKS0xNT1BRVFNUVVZXWFlaQUJDREVGR0hKS0xNT1BRVFNUVVZXWFlaQUJDREVGR0hKS0xNT1BRVFNUVVZXWA==");
        registry.add("ACCESS_TOKEN_EXPIRATION", () -> "36000000");
        registry.add("REFRESH_TOKEN_EXPIRATION", () -> "25204000");
        registry.add("USER_SERVICE_URL", () -> "http://mock");
        registry.add("ORDER_SERVICE_URL", () -> "http://mock");
        registry.add("PAYMENT_SERVICE_URL", () -> "http://mock");

    }
}
