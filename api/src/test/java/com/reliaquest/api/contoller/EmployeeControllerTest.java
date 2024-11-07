package com.reliaquest.api.contoller;

import com.reliaquest.api.controller.impl.EmployeeController;
import com.reliaquest.api.models.Employee;
import com.reliaquest.api.models.request.CreateEmployeeRequest;
import com.reliaquest.api.service.IEmployeeService;
import com.reliaquest.api.utils.TestSuitUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IEmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllEmployees_ShouldReturnEmployeeList() throws Exception {
        List<Employee> employees = Arrays.asList(TestSuitUtils.prepareEmployee());
        when(employeeService.getAllEmployees()).thenReturn(employees);

        mockMvc.perform(get("/employee"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].employee_name").value("test"))
                .andExpect(jsonPath("$[0].employee_salary").value(3000));
    }

    @Test
    void getEmployeesByNameSearch_ShouldReturnEmployeeList() throws Exception {
        List<Employee> employees = Arrays.asList(TestSuitUtils.prepareEmployee());
        when(employeeService.getEmployeesByNameSearch("te")).thenReturn(employees);

        mockMvc.perform(get("/employee/search/te"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employee_name").value("test"));
    }

    @Test
    void getEmployeeById_ShouldReturnEmployee() throws Exception {
        Employee employee = TestSuitUtils.prepareEmployee();
        when(employeeService.getEmployeeById("1")).thenReturn(employee);

        mockMvc.perform(get("/employee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee_name").value("test"))
                .andExpect(jsonPath("$.employee_salary").value(3000))
                .andExpect(jsonPath("$.employee_age").value(20));;
    }

    @Test
    void getHighestSalaryOfEmployees_ShouldReturnHighestSalary() throws Exception {
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(70000);

        mockMvc.perform(get("/employee/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(content().string("70000"));
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_ShouldReturnEmployeeNames() throws Exception {
        List<String> employeeNames = Arrays.asList("Virat Kohli", "Rohit Sharma");
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(employeeNames);

        mockMvc.perform(get("/employee/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Virat Kohli"))
                .andExpect(jsonPath("$[1]").value("Rohit Sharma"));
    }

    @Test
    void createEmployee_ShouldReturnCreatedEmployee() throws Exception {
        Employee employee = TestSuitUtils.prepareEmployee();
        when(employeeService.createEmployee(any(CreateEmployeeRequest.class))).thenReturn(employee);

        mockMvc.perform(post("/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"test\", \"salary\":50000, \"age\":20, \"title\":\"testTitle\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employee_name").value("test"))
                .andExpect(jsonPath("$.employee_salary").value(3000));
    }

    @Test
    void deleteEmployeeById_ShouldReturnNoContent() throws Exception {
        when(employeeService.deleteEmployeeById("1")).thenReturn("Employee deleted successfully");

        mockMvc.perform(delete("/employee/1"))
                .andExpect(status().isNoContent());
    }
}
