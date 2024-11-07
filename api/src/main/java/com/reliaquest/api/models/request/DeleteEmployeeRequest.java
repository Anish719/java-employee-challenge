package com.reliaquest.api.models.request;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class DeleteEmployeeRequest {
    private String name;

    public static DeleteEmployeeRequest from(@NonNull String name) {
        return DeleteEmployeeRequest.builder()
                .name(name)
                .build();
    }
}
