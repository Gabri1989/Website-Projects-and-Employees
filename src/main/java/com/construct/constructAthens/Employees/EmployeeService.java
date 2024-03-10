package com.construct.constructAthens.Employees;

import com.construct.constructAthens.AzureStorage.StorageService;
import com.construct.constructAthens.Employees.Employee_dependencies.Skill;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service

public class EmployeeService {

    private final EmployeeRepository employeeRepository ;
    @Value("${spring.cloud.azure.storage.blob.connection-string}")
    private String azureStorageConnectionString;
    private StorageService azureBlobAdapter;
    @Autowired

    private final ObjectMapper objectMapper;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, ObjectMapper objectMapper,StorageService azureBlobAdapter)        {
        this.employeeRepository = employeeRepository;
        this.azureBlobAdapter=azureBlobAdapter;
        this.objectMapper = objectMapper;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(UUID id)  {
        return employeeRepository.findById(id);

    }
    public Employee getEmployeeByUsername(String username){
        return employeeRepository.findEmployeeByUsername(username);
    }

    public Employee saveEmployee(Employee employee) {
        UUID userId=UUID.randomUUID();
        employee.setId(userId);
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(UUID id) {
        employeeRepository.deleteById(id);
    }
    public Collection<Skill> getSkillsByEmployeeId(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));

        return employee.getSkills();
    }


}