package com.construct.constructAthens.Employees;

import com.azure.core.exception.ResourceNotFoundException;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.construct.constructAthens.AzureStorage.StorageService;

import com.construct.constructAthens.Employees.Employee_dependencies.*;
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
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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

    @GetMapping("/employee/{id}/daily-time/{month}")
    public ResponseEntity<List<EmployeeTimeProjection>> getDailyAccumulatedTimePerMonth(@PathVariable("id") UUID employeeId, @PathVariable("month") int month) {
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
    @PostMapping("/checkIn/{employeeid}/{projectid}")
    public ResponseEntity<String> checkIn(@PathVariable("employeeid") UUID employeeid, @PathVariable("projectid") UUID projectid) {
        ZoneId zoneId = ZoneId.of("Europe/Athens");
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        LocalDate currentDate = now.toLocalDate();
        LocalTime checkInTime = now.toLocalTime();

        EmployeeTime existingEmployeeTime = employeeTimeGpsRepository.findByEmployeeIdAndDateAndProjectId(employeeid, currentDate, projectid);

        if (existingEmployeeTime != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("You have already checked in today for this project.");
        } else {
            EmployeeTime emp = new EmployeeTime();
            emp.setEmployeeId(employeeid);
            emp.setProjectId(projectid);
            emp.setCheckIn(checkInTime);
            emp.setAccumulatedTime(-15.0);
            emp.setDate(currentDate);
            employeeTimeGpsRepository.saveAndFlush(emp);
            return ResponseEntity.status(HttpStatus.OK).body("Check-in successful.");
        }
    }
    @PostMapping("/checkOut/{employeeid}/{projectid}")
    public ResponseEntity<String> checkOut(@PathVariable("employeeid") UUID employeeid, @PathVariable("projectid") UUID projectid) {
        ZoneId zoneId = ZoneId.of("Europe/Athens");
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        LocalDate currentDate = now.toLocalDate();
        LocalTime checkOutTime = now.toLocalTime();

        EmployeeTime existingEmployeeTime = employeeTimeGpsRepository.findByEmployeeIdAndDateAndProjectId(employeeid, currentDate, projectid);

        if (existingEmployeeTime != null) {
            existingEmployeeTime.setCheckOut(checkOutTime);
            employeeTimeGpsRepository.saveAndFlush(existingEmployeeTime);
            return ResponseEntity.status(HttpStatus.OK).body("Check-out successful.");
        } else {
            // Return a response indicating that check-in must be done first
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No check-in found for today for this project.");
        }
    }

    @PostMapping("/addLocation/{employeeid}/{projectid}")
    public ResponseEntity<String> addLocation(@PathVariable("employeeid") UUID employeeid, @PathVariable("projectid") UUID projectid, @RequestParam Double latitude, @RequestParam Double longitude) {
        Optional<Employee> employeeOptional = employeeRepository.findById(employeeid);
        if (employeeOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
        }

        ZoneId zoneId = ZoneId.of("Europe/Athens");
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        LocalDate currentDate = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime();

        EmployeeTime existingEmployeeTime = employeeTimeGpsRepository.findByEmployeeIdAndDateAndProjectId(employeeid, currentDate, projectid);

        Double accumulatedTime;
        if (existingEmployeeTime != null) {
            existingEmployeeTime.setLatitude(latitude);
            existingEmployeeTime.setLongitude(longitude);
            existingEmployeeTime.setTime(currentTime);
            accumulatedTime = existingEmployeeTime.getAccumulatedTime() + 15;
            existingEmployeeTime.setAccumulatedTime(accumulatedTime);
        } else {
            accumulatedTime = 0.0;
            existingEmployeeTime = new EmployeeTime();
            existingEmployeeTime.setEmployeeId(employeeid);
            existingEmployeeTime.setProjectId(projectid);
            existingEmployeeTime.setDate(currentDate);
            existingEmployeeTime.setTime(currentTime);
            existingEmployeeTime.setLatitude(latitude);
            existingEmployeeTime.setLongitude(longitude);
            existingEmployeeTime.setAccumulatedTime(accumulatedTime);
        }

        employeeTimeGpsRepository.saveAndFlush(existingEmployeeTime);
        return ResponseEntity.status(HttpStatus.OK).body("Location data added successfully");
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
