package com.construct.constructAthens.Employees;

import com.construct.constructAthens.security.UserInfoRepository;
import com.construct.constructAthens.security.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service

public class EmployeeService {

    private final EmployeeRepository employeeRepository ;
    @Value("${spring.cloud.azure.storage.blob.connection-string}")
    private String azureStorageConnectionString;

   // private UserInfoRepository userInfoRepository;
    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
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
    public EmployeeSkills getEmployeeSkillsById(UUID employeeId) {
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
        UUID userId=UUID.randomUUID();
        employee.setId(userId);
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(UUID id) {
        employeeRepository.deleteById(id);
    }

}