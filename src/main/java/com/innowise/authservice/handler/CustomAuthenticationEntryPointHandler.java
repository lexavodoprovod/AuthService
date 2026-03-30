package com.innowise.authservice.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.authservice.exception.ErrorDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException e) throws IOException, ServletException {

        HttpStatus status = HttpStatus.UNAUTHORIZED;
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
