package com.reliaquest.api.models.request;

import com.reliaquest.api.constants.ErrorConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Data;

@Data
public class CreateEmployeeRequest {

    @NotBlank(message = ErrorConstants.BLANK_NAME_MESSAGE)
    private String name;

    @Positive
    @NotNull(message = ErrorConstants.BLANK_SALARY_MESSAGE)
    private Integer salary;

    @Min(value = 16, message = ErrorConstants.INVALID_AGE_MESSAGE)
    @Max(value = 75, message = ErrorConstants.INVALID_AGE_MESSAGE)
    @NotNull(message = ErrorConstants.BLANK_AGE_MESSAGE)
    private Integer age;

    @NotBlank(message = ErrorConstants.BLANK_TITLE_MESSAGE)
    private String title;
}
