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

@Slf4j
@Service

public class EmployeeService {

    private final EmployeeRepository employeeRepository;
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

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
   /* public boolean partialUpdate(Long id, String key, String value)
            throws NotFoundEx {
        log.info("Search id={}", id);
        Optional<Employee> optional = employeeRepository.findById(id);
        if (optional.isPresent()) {
            Employee user = optional.get();

            if (key.equalsIgnoreCase("name")) {
                log.info("Updating full name");
                user.setName(value);
            }
            if (key.equalsIgnoreCase("image")) {
                log.info("Updating image");
                user.setImageURL(value);
            }
            if (key.equalsIgnoreCase("email")) {
                log.info("Updating email");
                user.setEmail(value);
            }
            if (key.equalsIgnoreCase("number")) {
                log.info("Updating number");
                user.setNumber(value);
            }
            if (key.equalsIgnoreCase("adress")) {
                log.info("Updating adress");
                user.setAdress(value);
            }
            if (key.equalsIgnoreCase("birthday")) {
                log.info("Updating birthday");

                user.setBirthday(LocalDate.parse( value));
            }
            if (key.equalsIgnoreCase("nationality")) {
                log.info("Updating nationality");
                user.setNationality( value);
            }
            if (key.equalsIgnoreCase("kids")) {
                log.info("Updating kids");
                user.setKids(Integer.parseInt(value));
            }

            if (key.equalsIgnoreCase("emergencyContact")) {
                log.info("Updating emergencyContact");
                user.setEmergencyContact(value);
            }
            if (key.equalsIgnoreCase("emergencyPhone")) {
                log.info("Updating emergencyPhone");
                user.setEmergencyPhone(value);
            }

            if (key.equalsIgnoreCase("adress")) {
                log.info("Updating adress");
                user.setAdress(value);
            }
            if (key.equalsIgnoreCase("adress")) {
                log.info("Updating adress");
                user.setAdress(value);
            }
            if (key.equalsIgnoreCase("employmentDate")) {
                log.info("Updating employmentDate");
                user.setEmploymentDate(LocalDate.parse(value));
            }

            employeeRepository.save(user);
            return true;
        } else {
            throw new NotFoundEx("RESOURCE_NOT_FOUND");
        }
    }*/


}