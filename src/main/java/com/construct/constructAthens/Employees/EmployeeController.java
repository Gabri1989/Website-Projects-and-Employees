package com.construct.constructAthens.Employees;

import com.construct.constructAthens.AzureStorage.StorageService;

import com.construct.constructAthens.Employees.Employee_dependencies.Skill;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeController{
    private final ObjectMapper objectMapper;
    @Autowired
    private final EmployeeService employeeService;

    @Autowired
    private StorageService azureBlobStorageService;
    @Autowired
    public EmployeeController(ObjectMapper objectMapper, EmployeeService employeeService) {
        this.objectMapper = objectMapper;
        this.employeeService = employeeService;

    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable UUID id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        return employee.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

   /* @PostMapping(path="/createEmployee",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Employee> createEmployee(
            @RequestParam("imageURL") MultipartFile imageURL,
            @RequestParam("curiculum") MultipartFile curiculum,
            @RequestParam("signature") MultipartFile signature,
            @PathVariable UUID uuid) throws IOException {
            // CONVERSIA BASE64 SAU fILE
        String imageUrl = azureBlobStorageService.upload(imageURL);
        String curriculumUrl = azureBlobStorageService.upload(curiculum);
        String signatureUrl = azureBlobStorageService.upload(signature);

        employee.setImageURL(imageUrl);
        employee.setCuriculum(curriculumUrl);
        employee.setSignature(signatureUrl);
        Employee savedEmployee = employeeService.saveEmployee(employee);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }*/

   @PostMapping("/createEmployee")
   public Employee saveEmployee(@RequestBody Employee employee) {
       UUID userId = UUID.randomUUID();
       employee.setId(userId);

       return employeeService.saveEmployee(employee);
   }
    @PutMapping("/edit/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable UUID id, @RequestBody Employee updatedEmployee) {
        Optional<Employee> existingEmployee = employeeService.getEmployeeById(id);
        if (existingEmployee.isPresent()) {
            updatedEmployee.setId(id);
            Employee savedEmployee = employeeService.saveEmployee(updatedEmployee);

            return new ResponseEntity<>(savedEmployee, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        if (employee.isPresent()) {
            employeeService.deleteEmployee(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/{employeeId}/skills")
    public Collection<Skill> getSkillsByEmployeeId(@PathVariable UUID employeeId) {
        return employeeService.getSkillsByEmployeeId(employeeId);
    }


}
