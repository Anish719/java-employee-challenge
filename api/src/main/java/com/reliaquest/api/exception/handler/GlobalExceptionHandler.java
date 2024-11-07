package com.reliaquest.api.exception.handler;

import com.reliaquest.api.constants.ErrorConstants;
import com.reliaquest.api.exception.EmployeeErrorResponse;
import com.reliaquest.api.exception.EmployeeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<EmployeeErrorResponse> handleHttpClientErrorException(HttpClientErrorException ex, WebRequest request) {

        EmployeeErrorResponse errorResponse = new EmployeeErrorResponse();
        errorResponse.setErrorCode(ex.getStatusCode().value());
        errorResponse.setTimestamp(LocalDateTime.now());

        switch (ex.getStatusCode().value()) {
            case 404 -> errorResponse.setErrorMessage(List.of(ErrorConstants.DATA_NOT_FOUND));
            case 429 -> errorResponse.setErrorMessage(List.of(ErrorConstants.TOO_MANY_REQUESTS));
        }

        log.error("Error Occurred while performing operation {}", errorResponse);

        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<EmployeeErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        EmployeeErrorResponse errorResponse = new EmployeeErrorResponse();
        errorResponse.setErrorCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setErrorMessage(errorMessages);
        errorResponse.setTimestamp(LocalDateTime.now());

        log.error("Bad Request while hitting the API with error {}", errorResponse);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmployeeException.class)
    public ResponseEntity<?> handleEmployeeException(EmployeeException ex, WebRequest request) {
        EmployeeErrorResponse errorResponse = new EmployeeErrorResponse();
        errorResponse.setErrorCode(ex.getHttpStatusCode().value());
        errorResponse.setErrorMessage(List.of(ex.getMessage()));
        errorResponse.setTimestamp(LocalDateTime.now());

        log.error("Error Occurred while performing operation {}", errorResponse);

        return new ResponseEntity<>(errorResponse, ex.getHttpStatusCode());
    }
}

