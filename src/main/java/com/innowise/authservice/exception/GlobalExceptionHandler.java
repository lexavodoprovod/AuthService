package com.innowise.authservice.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final String MESSAGE_KEY = "message";
    private static final String VALIDATION_ERROR = "Validation Error";

    private final ObjectMapper mapper;

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorDetails> handleFeignException(FeignException e) {

        String errorBody = e.contentUTF8();
        String message;

        try {
            Map<String, Object> map = mapper.readValue(errorBody, Map.class);

            if (map.containsKey(MESSAGE_KEY)) {
                message = map.get(MESSAGE_KEY).toString();
            } else {
                message = errorBody;
            }
        } catch (Exception parseException) {
            message = e.getMessage();
        }

        HttpStatus status = HttpStatus.resolve(e.status());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        ErrorDetails exception = ErrorDetails.builder()
                .message(message)
                .errorName(status.getReasonPhrase())
                .httpStatus(status.value())
                .timestamp(LocalDateTime.now())
                .build();


        return new ResponseEntity<>(exception, status);
    }

    @ExceptionHandler(InternalServiceException.class)
    public ResponseEntity<ErrorDetails> handleNotFound(InternalServiceException e) {
        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
        ErrorDetails exception = createErrorDetails(e, status);
        return  new ResponseEntity<>(exception, status);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleNotFound(EntityNotFoundException e) {
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        ErrorDetails exception = createErrorDetails(e, notFound);
        return  new ResponseEntity<>(exception, notFound);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorDetails> handleConflict(BusinessException e) {
        HttpStatus httpError = e.getStatus();
        ErrorDetails exception = createErrorDetails(e, httpError);
        return  new ResponseEntity<>(exception, httpError);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorDetails> handleJwtException(JwtException e) {
        HttpStatus httpError = HttpStatus.UNAUTHORIZED;
        ErrorDetails exception = createErrorDetails(e, httpError);
        return new ResponseEntity<>(exception, httpError);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleUsernameException(UsernameNotFoundException e) {
        HttpStatus httpError = HttpStatus.UNAUTHORIZED;
        ErrorDetails exception = createErrorDetails(e, httpError);
        return new ResponseEntity<>(exception, httpError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleValidation(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorDetails error = ErrorDetails.builder()
                .message(errorMessage)
                .errorName(VALIDATION_ERROR)
                .httpStatus(status.value())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, status);
    }

    private ErrorDetails createErrorDetails(Exception e, HttpStatus status) {
        return ErrorDetails.builder()
                .message(e.getMessage())
                .errorName(status.getReasonPhrase())
                .httpStatus(status.value())
                .timestamp(LocalDateTime.now())
                .build();
    }


}
