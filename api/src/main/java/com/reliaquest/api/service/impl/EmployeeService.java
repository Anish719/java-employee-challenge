package com.reliaquest.api.service.impl;

import com.reliaquest.api.constants.APIConstants;
import com.reliaquest.api.constants.ErrorConstants;
import com.reliaquest.api.exception.EmployeeException;
import com.reliaquest.api.models.Employee;
import com.reliaquest.api.models.request.CreateEmployeeRequest;
import com.reliaquest.api.models.request.DeleteEmployeeRequest;
import com.reliaquest.api.models.response.APIResponse;
import com.reliaquest.api.service.IEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeeService implements IEmployeeService {

    private final RestTemplate restTemplate;

    public EmployeeService(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Method to retrieves a list of all employees.
     *
     * @return a list of all employees. Returns an empty list if there are no employees available.
     */
    @Override
    @Cacheable(value = APIConstants.GET_ALL_EMPLOYEES_CACHE_KEY)
    public List<Employee> getAllEmployees() {
        log.debug("[EmployeeService] :: Fetching All Employees");
        return fetchEmployeeList();
    }

    /**
     * Method to retrieves list of employees whose names contain the given search string.
     * The search is case-insensitive.
     *
     * @param searchString the string to search for.
     * @return a list of employees whose names contain the given search string.
     * Returns an empty list if no employees match.
     */
    @Override
    @Cacheable(value = APIConstants.GET_ALL_EMPLOYEES_BY_NAME_CACHE_KEY)
    public List<Employee> getEmployeesByNameSearch(String searchString) {
        log.debug("[EmployeeService] :: Searching Employees by Name '{}'", searchString);
        return getAllEmployees().stream()
                .filter(employee -> employee.getName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Method to retrieve an employee by their ID.
     *
     * @param id the unique id of the employee.
     * @return the employee with the specified ID.
     * @throws EmployeeException if no employee is found with the given ID.
     */
    @Override
    @Cacheable(value = APIConstants.GET_EMPLOYEE_BY_ID_CACHE_KEY, key = "#id")
    public Employee getEmployeeById(String id) {
        log.debug("[EmployeeService] :: Fetching Employee by ID '{}'", id);
        String url = APIConstants.EMPLOYEE_BASE_URL + "/" + id;
        return fetchEmployee(url, id);
    }

    /**
     * Method to retrieve the highest salary among all employees.
     *
     * @return the highest salary of all employees.
     * @throws EmployeeException if no employees are found, preventing the calculation of the highest salary.
     */
    @Override
    @Cacheable(value = APIConstants.GET_HIGHEST_SALARY_CACHE_KEY)
    public Integer getHighestSalaryOfEmployees() {
        log.debug("[EmployeeService] :: Fetching Highest Salary of Employees");
        return getAllEmployees().stream()
                .map(Employee::getSalary)
                .max(Integer::compareTo)
                .orElseThrow(() -> new EmployeeException(ErrorConstants.NO_EMPLOYEE_FOR_CALCULATION, HttpStatusCode.valueOf(404)));
    }

    /**
     * Method to retrieve the names of the top ten highest earning employees.
     * The employees are sorted in descending order based on salary.
     *
     * @return a list of the names of the top ten highest earning employees.
     * If the employees are less 10, all employee names will be returned.
     */
    @Override
    @Cacheable(value = APIConstants.GET_TOP_TEN_HIGHEST_SALARY_NAME_KEY)
    public List<String> getTopTenHighestEarningEmployeeNames() {
        log.debug("[EmployeeService] :: Fetching Top Ten Highest Salaried Employees");
        return Optional.ofNullable(getAllEmployees())
                .orElseGet(List::of)
                .stream()
                .sorted(Comparator.comparingInt(Employee::getSalary).reversed())
                .limit(10)
                .map(Employee::getName)
                .collect(Collectors.toList());
    }

    /**
     * Method to create a new employee.
     *
     * @param createEmployeeRequest the request containing the details of the employee to be created.
     * @return the newly created employee.
     * @throws EmployeeException if the employee creation fails.
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = APIConstants.GET_ALL_EMPLOYEES_CACHE_KEY, allEntries = true),
            @CacheEvict(value = APIConstants.GET_ALL_EMPLOYEES_BY_NAME_CACHE_KEY, allEntries = true),
            @CacheEvict(value = APIConstants.GET_HIGHEST_SALARY_CACHE_KEY, allEntries = true),
            @CacheEvict(value = APIConstants.GET_TOP_TEN_HIGHEST_SALARY_NAME_KEY, allEntries = true)
    })
    public Employee createEmployee(CreateEmployeeRequest createEmployeeRequest) {
        log.debug("[EmployeeService] :: Creating New Employee");
        return postRequest(createEmployeeRequest);
    }

    /**
     * Method to delete a employee based on id.
     *
     * @param id the unique id of the employee..
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = APIConstants.GET_ALL_EMPLOYEES_CACHE_KEY, allEntries = true),
            @CacheEvict(value = APIConstants.GET_ALL_EMPLOYEES_BY_NAME_CACHE_KEY, allEntries = true),
            @CacheEvict(value = APIConstants.GET_HIGHEST_SALARY_CACHE_KEY, allEntries = true),
            @CacheEvict(value = APIConstants.GET_TOP_TEN_HIGHEST_SALARY_NAME_KEY, allEntries = true),
            @CacheEvict(value = APIConstants.GET_EMPLOYEE_BY_ID_CACHE_KEY, key = "#id")
    })
    public String deleteEmployeeById(String id) {
        log.debug("[EmployeeService] :: Deleting Employee by ID '{}'", id);
        Employee employeeById = getEmployeeById(id);
        if (employeeById != null) {
            DeleteEmployeeRequest request = DeleteEmployeeRequest.from(employeeById.getName());
            deleteRequest(request);
        }
        return null;
    }

    private List<Employee> fetchEmployeeList() {
        return fetchData(APIConstants.EMPLOYEE_BASE_URL, new ParameterizedTypeReference<>() {
        }, "No employees found.");
    }

    private Employee fetchEmployee(String url, String id) {
        return fetchData(url, new ParameterizedTypeReference<>() {
        }, "Employee not found with ID: " + id);
    }

    private <T> T fetchData(String url, ParameterizedTypeReference<APIResponse<T>> responseType, String errorMessage) {
        ResponseEntity<APIResponse<T>> response = restTemplate.exchange(url, HttpMethod.GET, null, responseType);
        return Optional.ofNullable(response.getBody())
                .map(APIResponse::getData)
                .orElseThrow(() -> new EmployeeException(errorMessage));
    }

    private Employee postRequest(CreateEmployeeRequest request) {
        HttpEntity<CreateEmployeeRequest> requestEntity = new HttpEntity<>(request);
        ResponseEntity<APIResponse<Employee>> response = restTemplate.exchange(APIConstants.EMPLOYEE_BASE_URL, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {
        });
        return Optional.ofNullable(response.getBody())
                .map(APIResponse::getData)
                .orElseThrow(() -> new EmployeeException(ErrorConstants.FAILED_TO_CREATE_EMPLOYEE));
    }

    private void deleteRequest(DeleteEmployeeRequest request) {
        HttpEntity<DeleteEmployeeRequest> requestEntity = new HttpEntity<>(request);
        restTemplate.exchange(APIConstants.EMPLOYEE_BASE_URL, HttpMethod.DELETE, requestEntity, new ParameterizedTypeReference<>() {
        });
    }
}
