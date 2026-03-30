package com.innowise.authservice.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.authservice.exception.ErrorDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException e) throws IOException, ServletException {

        HttpStatus status = HttpStatus.FORBIDDEN;
        ErrorDetails exception = ErrorDetails.builder()
                .message(e.getMessage())
                .errorName(status.getReasonPhrase())
                .httpStatus(status.value())
                .timestamp(LocalDateTime.now())
                .build();

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String json = objectMapper.writeValueAsString(exception);

        response.getWriter().write(json);
    }
}
