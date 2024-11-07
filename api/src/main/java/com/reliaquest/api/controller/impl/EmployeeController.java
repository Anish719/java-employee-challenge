package com.reliaquest.api.controller.impl;

import com.reliaquest.api.controller.IEmployeeController;
import com.reliaquest.api.models.Employee;
import com.reliaquest.api.models.request.CreateEmployeeRequest;
import com.reliaquest.api.service.IEmployeeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
@RequestMapping("/employee")
@Slf4j
public class EmployeeController implements IEmployeeController<Employee, CreateEmployeeRequest> {

    private final IEmployeeService employeeService;

    public EmployeeController(final IEmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        log.info("[EmployeeController] :: Getting All Employees");
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        log.info("[EmployeeController] :: Get Employees By Search for search String {}", searchString);
        return ResponseEntity.ok(employeeService.getEmployeesByNameSearch(searchString));
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        log.info("[EmployeeController] :: Get Employee for id {}", id);
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        log.info("[EmployeeController] :: Get Highest Salary of Employee");
        return ResponseEntity.ok(employeeService.getHighestSalaryOfEmployees());
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        log.info("[EmployeeController] :: Get Top Ten Highest Salaried Employees");
        return ResponseEntity.ok(employeeService.getTopTenHighestEarningEmployeeNames());
    }

    @Override
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody CreateEmployeeRequest createEmployeeRequest) {
        log.info("[EmployeeController] :: Creating Employee");
        return new ResponseEntity<>(employeeService.createEmployee(createEmployeeRequest), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        log.info("[EmployeeController] :: Deleting Employee with id {}", id);
        return new ResponseEntity<>(employeeService.deleteEmployeeById(id), HttpStatus.NO_CONTENT);
    }
}
