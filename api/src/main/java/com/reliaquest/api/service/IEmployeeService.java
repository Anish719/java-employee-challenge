package com.reliaquest.api.service;

import com.reliaquest.api.models.request.CreateEmployeeRequest;
import com.reliaquest.api.models.Employee;

import java.util.List;

public interface IEmployeeService {
    List<Employee> getAllEmployees();

    List<Employee> getEmployeesByNameSearch(String searchString);

    Employee getEmployeeById(String id);

    Integer getHighestSalaryOfEmployees();

    List<String> getTopTenHighestEarningEmployeeNames();

    Employee createEmployee(CreateEmployeeRequest createEmployeeRequest);

    String deleteEmployeeById(String id);
}
