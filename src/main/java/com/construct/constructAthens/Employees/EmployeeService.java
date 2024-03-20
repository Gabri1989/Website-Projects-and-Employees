package com.construct.constructAthens.Employees;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobProperties;
import com.construct.constructAthens.AzureStorage.StorageService;
import com.construct.constructAthens.Employees.Employee_dependencies.Skill;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service

public class EmployeeService {

    private final EmployeeRepository employeeRepository ;
    @Value("${spring.cloud.azure.storage.blob.connection-string}")
    private String azureStorageConnectionString;
    @Autowired
    private BlobContainerClient blobContainerClient;


    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository)        {
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

    public Employee saveEmployee(Employee employee) {
        //UUID userId = UUID.randomUUID();
        //employee.setId(userId);
        //String imageURL = "https://ipstorage1989.blob.core.windows.net/atenacontainer/"+ userId+"png";
       // employee.setImageURL(imageURL);
        return employeeRepository.save(employee);
    }
   /* public Employee saveEmployee(Employee employee) {
        UUID userId = UUID.randomUUID();
        employee.setId(userId);

        // Get the list of blobs from the Azure container
        List<String> blobNames = listBlobs();

        // Sort the list of blob names based on their last modified timestamp
        List<String> sortedBlobNames = blobNames.stream()
                .sorted(Comparator.comparing(this::getLastModifiedTimestamp).reversed())
                .collect(Collectors.toList());

        // Extract the filename part from the last blob's URL
        String lastBlobName = "";
        if (!sortedBlobNames.isEmpty()) {
            lastBlobName = sortedBlobNames.get(0);
            lastBlobName = lastBlobName.substring(lastBlobName.lastIndexOf('/') + 1);
        }

        // Construct the imageURL for the employee
        String imageURL = "https://ipstorage1989.blob.core.windows.net/atenacontainer/" + lastBlobName;
        employee.setImageURL(imageURL);

        return employeeRepository.save(employee);
    }

    // Method to list blobs in the container
    private List<String> listBlobs() {
        return blobContainerClient.listBlobs().stream()
                .map(blobItem -> blobItem.getName())
                .collect(Collectors.toList());
    }
    private long getLastModifiedTimestamp(String blobName) {
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
        BlobProperties properties = blobClient.getProperties();
        OffsetDateTime lastModified = properties.getLastModified();
        return lastModified.toInstant().toEpochMilli();
    }*/

    public void deleteEmployee(UUID id) {
        employeeRepository.deleteById(id);
    }
    public Collection<Skill> getSkillsByEmployeeId(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));

        return employee.getSkills();
    }
    public Employee updateEmployeeByFields(UUID id, Map<String, Object> fields) {
        Optional<Employee> existingEmployee = employeeRepository.findById(id);

        if (existingEmployee.isPresent()) {
            fields.forEach((key, value) -> {
                Field field = ReflectionUtils.findField(Employee.class, key);
                assert field != null;
                field.setAccessible(true);
                ReflectionUtils.setField(field, existingEmployee.get(), value);
            });
            return employeeRepository.save(existingEmployee.get());
        }
        return null;
    }


}