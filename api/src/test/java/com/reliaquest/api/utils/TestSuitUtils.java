package com.reliaquest.api.utils;

import com.reliaquest.api.models.Employee;

import java.util.UUID;

public class TestSuitUtils {

    public static Employee prepareEmployee() {
        return Employee.builder()
                .id(UUID.randomUUID())
                .age(20).email("test@test.com")
                .name("test")
                .salary(3000)
                .title("testTitle").build();
    }
}
