package com.innowise.authservice.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.authservice.exception.ErrorDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@RequiredArgsConstructor
@Component
public final class HandlerExceptionJsonConverter {

    private final ObjectMapper objectMapper;

    public String convertExceptionAndStatusToJson(Exception e, HttpStatus status) throws JsonProcessingException {

        ErrorDetails exception = ErrorDetails.builder()
                .message(e.getMessage())
                .errorName(status.getReasonPhrase())
                .httpStatus(status.value())
                .timestamp(LocalDateTime.now())
                .build();

        return objectMapper.writeValueAsString(exception);
    }
}
