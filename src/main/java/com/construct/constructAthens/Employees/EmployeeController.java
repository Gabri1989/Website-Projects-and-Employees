package com.construct.constructAthens.Employees;

import com.azure.core.exception.ResourceNotFoundException;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.construct.constructAthens.AzureStorage.StorageService;

import com.construct.constructAthens.Employees.Employee_dependencies.EmployeeTime;
import com.construct.constructAthens.Employees.Employee_dependencies.EmployeeTimeGpsRepository;
import com.construct.constructAthens.Employees.Employee_dependencies.EmployeeTimeProjection;
import com.construct.constructAthens.Employees.Employee_dependencies.Skill;
import com.construct.constructAthens.Employees.exception.EmployeeNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeController{
    @Autowired
    private final EmployeeService employeeService;
    @Autowired
    private final EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeTimeGpsRepository employeeTimeGpsRepository;


    @Autowired
    public EmployeeController( EmployeeService employeeService, EmployeeRepository employeeRepository,EmployeeTimeGpsRepository employeeTimeGpsRepository) {
        this.employeeService = employeeService;
        this.employeeRepository = employeeRepository;
        this.employeeTimeGpsRepository=employeeTimeGpsRepository;
    }
    //@PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    //@PreAuthorize("hasAuthority('ROLE_EMPLOYEE') or hasAuthority('ROLE_ADMIN')")

    
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable UUID id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        return employee.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
/*    @GetMapping("/employee/{id}/accumulated-time/{month}")
    public ResponseEntity<List<EmployeeTimeProjection>> getAccumulatedTimePerDay(@PathVariable("id") UUID employeeId, @PathVariable("month") int month) {
        List<EmployeeTimeProjection> accumulatedTimeList = employeeTimeGpsRepository.getAccumulatedTimePerDay(employeeId, month);
        if (accumulatedTimeList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(accumulatedTimeList);
    }*/
@GetMapping("/employee/{id}/accumulated-time/{month}")
public ResponseEntity<List<EmployeeTimeProjection>> getAccumulatedTimePerDay(@PathVariable("id") UUID employeeId, @PathVariable("month") int month) {
    List<EmployeeTimeProjection> accumulatedTimeList = employeeTimeGpsRepository.getAccumulatedTimePerDay(employeeId, month);
    if (accumulatedTimeList.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    return ResponseEntity.status(HttpStatus.OK).body(accumulatedTimeList);
}
    @PostMapping("/createEmployee")
    public Employee saveEmployee(@RequestBody Employee employee) {
        UUID userId = UUID.randomUUID();
        employee.setId(userId);
        return employeeService.saveEmployee(employee);
    }
  /*  @PostMapping("/addLocation/{id}")
    public ResponseEntity<Employee> addLocation(@PathVariable("id") UUID id, @RequestParam Double latitude, @RequestParam Double longitude) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));
        employee.setLatitude(latitude);
        employee.setLongitude(longitude);

        Double accumulatedTime = employee.getTimegps();
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Retrieve the accumulated time for the current day
        List<EmployeeTime> employeeTimeList = employeeTimeGpsRepository.findByEmployeeIdAndDateBetween(id, Date.from(currentDateTime.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(currentDateTime.toLocalDate().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        // Update the accumulated time for the current day
        for (EmployeeTime employeeTime : employeeTimeList) {
            accumulatedTime += employeeTime.getAccumulatedTime();
        }
        accumulatedTime += 10.0; // Add 10 minutes
        employee.setTimegps(accumulatedTime);
        employeeRepository.save(employee);

        // Save or update the EmployeeTime entries for the current day
        boolean existingEntryUpdated = false;
        for (EmployeeTime employeeTime : employeeTimeList) {
            if (employeeTime.getDate().equals(Date.from(currentDateTime.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
                employeeTime.setAccumulatedTime(accumulatedTime);
                employeeTimeGpsRepository.save(employeeTime);
                existingEntryUpdated = true;
                break;
            }
        }

        if (!existingEntryUpdated) {
            EmployeeTime employeeTime = new EmployeeTime();
            employeeTime.setEmployeeId(id);
            employeeTime.setDate(Date.from(currentDateTime.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            employeeTime.setAccumulatedTime(accumulatedTime);
            employeeTimeGpsRepository.save(employeeTime);
        }

        return ResponseEntity.status(HttpStatus.OK).body(employee);
    }*/
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


   // @PreAuthorize("hasAuthority('ROLE_ADMIN')")
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
    //@PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/employees/{employeeId}/skills/{skillName}")
    public void deleteSkillBySkillName(@PathVariable UUID employeeId, @PathVariable String skillName) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));

        boolean removed = employee.getSkills().removeIf(skill -> skill.getSkillName().equals(skillName));

        if (!removed) {
            throw new EntityNotFoundException("Skill not found with name: " + skillName);
        }
        employeeRepository.save(employee);
    }

    //@PreAuthorize("hasAuthority('ROLE_EMPLOYEE') or hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/{id}")
   public Employee updateEmployeeFields(@PathVariable UUID id, @RequestBody Map<String, Object> fields) {
       return employeeService.updateEmployeeByFields(id, fields);
   }


}
