package com.reliaquest.api.exception;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EmployeeErrorResponse {
    private int errorCode;
    private List<String> errorMessage;
    private LocalDateTime timestamp;
}
