package com.reliaquest.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class EmployeeException extends RuntimeException {
    private HttpStatusCode httpStatusCode;

    public EmployeeException() {

    }

    public EmployeeException(String message) {
        super(message);
    }

    public EmployeeException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmployeeException(String message, HttpStatusCode code) {
        super(message);
        this.httpStatusCode = code;
    }


}
