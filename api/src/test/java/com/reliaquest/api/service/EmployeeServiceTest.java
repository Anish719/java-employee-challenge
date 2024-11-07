package com.reliaquest.api.service;

import com.reliaquest.api.constants.APIConstants;
import com.reliaquest.api.constants.ErrorConstants;
import com.reliaquest.api.exception.EmployeeException;
import com.reliaquest.api.models.Employee;
import com.reliaquest.api.models.request.CreateEmployeeRequest;
import com.reliaquest.api.models.response.APIResponse;
import com.reliaquest.api.service.impl.EmployeeService;
import com.reliaquest.api.utils.TestSuitUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EmployeeService employeeService;

    private List<Employee> mockEmployees;
    private Employee mockEmployee;

    @BeforeEach
    void setUp() {
        mockEmployee = TestSuitUtils.prepareEmployee();
        mockEmployees = Arrays.asList(mockEmployee);
    }

    private <T> ResponseEntity<APIResponse<T>> createMockResponse(T data) {
        APIResponse<T> response = new APIResponse<>();
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    @Test
    void testGetAllEmployees_Success() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(createMockResponse(mockEmployees));

        List<Employee> employees = employeeService.getAllEmployees();

        assertNotNull(employees);
        assertEquals(1, employees.size());
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class));
    }

    @Test
    void testGetAllEmployees_EmptyList() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(createMockResponse(List.of()));

        List<Employee> employees = employeeService.getAllEmployees();

        assertNotNull(employees);
        assertTrue(employees.isEmpty());
    }

    @Test
    void testGetEmployeesByNameSearch_Found() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(createMockResponse(mockEmployees));

        List<Employee> employees = employeeService.getEmployeesByNameSearch("te");

        assertNotNull(employees);
        assertEquals(1, employees.size());
        assertTrue(employees.stream().allMatch(e -> e.getName().toLowerCase().contains("te")));
    }

    @Test
    void testGetEmployeesByNameSearch_NotFound() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(createMockResponse(mockEmployees));

        List<Employee> employees = employeeService.getEmployeesByNameSearch("nonexistent");

        assertNotNull(employees);
        assertTrue(employees.isEmpty());
    }

    @Test
    void testGetEmployeeById_Found() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(createMockResponse(mockEmployee));

        Employee employee = employeeService.getEmployeeById(String.valueOf(UUID.randomUUID()));

        assertNotNull(employee);
        assertEquals("test", employee.getName());
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class));
    }

    @Test
    void testGetEmployeeById_NotFound() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenThrow(new EmployeeException("Employee not found with ID: 99"));

        EmployeeException exception = assertThrows(EmployeeException.class, () -> employeeService.getEmployeeById("99"));
        assertEquals("Employee not found with ID: 99", exception.getMessage());
    }

    @Test
    void testGetHighestSalaryOfEmployees_Success() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(createMockResponse(mockEmployees));

        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();

        assertEquals(3000, highestSalary);
    }

    @Test
    void testGetHighestSalaryOfEmployees_EmptyList() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(createMockResponse(List.of()));

        EmployeeException exception = assertThrows(EmployeeException.class, () -> employeeService.getHighestSalaryOfEmployees());
        assertEquals(ErrorConstants.NO_EMPLOYEE_FOR_CALCULATION, exception.getMessage());
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(createMockResponse(mockEmployees));

        List<String> topEarnerNames = employeeService.getTopTenHighestEarningEmployeeNames();

        assertNotNull(topEarnerNames);
        assertEquals(1, topEarnerNames.size());
        assertEquals("test", topEarnerNames.get(0));  // Highest salary should be first
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames_NotFound() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(createMockResponse(List.of()));

        List<String> topEarnerNames = employeeService.getTopTenHighestEarningEmployeeNames();

        assertEquals(0, topEarnerNames.size());
    }

    @Test
    void testCreateEmployee_Success() {
        CreateEmployeeRequest request = new CreateEmployeeRequest();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(createMockResponse(mockEmployee));

        Employee createdEmployee = employeeService.createEmployee(request);

        assertNotNull(createdEmployee);
        assertEquals("test", createdEmployee.getName());
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class));
    }

    @Test
    void testDeleteEmployeeById_Success() {
        String url = APIConstants.EMPLOYEE_BASE_URL + "/1";

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(createMockResponse(mockEmployee));

        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.noContent().build());

        String response = employeeService.deleteEmployeeById("1");

        assertNull(response);

        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class));
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), any(ParameterizedTypeReference.class));
    }
}

