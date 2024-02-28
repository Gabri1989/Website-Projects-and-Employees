package com.construct.constructAthens.Employees;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.construct.constructAthens.Employees.exception.NotFoundEx;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service

public class EmployeeService {

    private final EmployeeRepository employeeRepository ;
    @Value("${spring.cloud.azure.storage.blob.connection-string}")
    private String azureStorageConnectionString;
    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(Long id)  {
        return employeeRepository.findById(id);

    }
    public EmployeeSkills getEmployeeSkillsById(Long employeeId) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);

        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            return mapToEmployeeSkills(employee);
        } else {
            return null;
        }
    }

    private EmployeeSkills mapToEmployeeSkills(Employee employee) {
        EmployeeSkills employeeSkills = new EmployeeSkills();
        employeeSkills.setSkillName(employee.getSkillName());
        employeeSkills.setLevel(employee.getLevel());
        employeeSkills.setExperience(employee.getExperience());
        return employeeSkills;
    }
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

}