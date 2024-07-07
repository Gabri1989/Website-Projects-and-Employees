package com.construct.constructAthens.Employees;
import com.construct.constructAthens.Employees.Employee_dependencies.*;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeController{
    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final EmployeeTimeGpsRepository employeeTimeGpsRepository;

    public EmployeeController( EmployeeService employeeService, EmployeeRepository employeeRepository,EmployeeTimeGpsRepository employeeTimeGpsRepository) {
        this.employeeService = employeeService;
        this.employeeRepository = employeeRepository;
        this.employeeTimeGpsRepository=employeeTimeGpsRepository;
    }
   // @RolesAllowed({"ROLE_ADMIN"})
    @GetMapping("/allEmployees")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    //@RolesAllowed({"ROLE_ADMIN"})
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable UUID id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        return employee.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    //@RolesAllowed({"ROLE_ADMIN"})
    @GetMapping("/employee/{employeeId}/project/{projectId}/daily-time/{month}")
    public ResponseEntity<List<EmployeeTimeProjection>> getDailyAccumulatedTimePerMonth(
            @PathVariable("employeeId") UUID employeeId,
            @PathVariable("projectId") UUID projectId,
            @PathVariable("month") int month) {
        List<EmployeeTimeProjection> accumulatedTimeList = employeeTimeGpsRepository.getAccumulatedTimePerDay(employeeId, projectId, month);
        if (accumulatedTimeList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(accumulatedTimeList);
    }

   // @RolesAllowed({"ROLE_ADMIN"})
    @PostMapping("/createEmployee")
    public Employee saveEmployee(@RequestBody Employee employee) {
        UUID userId = UUID.randomUUID();
        employee.setId(userId);
        return employeeService.saveEmployee(employee);
    }
   // @RolesAllowed({"ROLE_EMPLOEE","ROLE_ADMIN"})
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
            emp.setAccumulatedTime(0.0);
            emp.setDate(currentDate);
            employeeTimeGpsRepository.saveAndFlush(emp);
            return ResponseEntity.status(HttpStatus.OK).body("Check-in successful.");
        }
    }
   // @RolesAllowed({"ROLE_EMPLOEE","ROLE_ADMIN"})
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
   // @RolesAllowed({"ROLE_EMPLOEE","ROLE_ADMIN"})
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
   // @RolesAllowed({"ROLE_EMPLOEE","ROLE_ADMIN"})
   @PutMapping("/editEmployee/{id}")
   public ResponseEntity<Employee> updateEmployee(@PathVariable UUID id, @RequestBody EmployeeDTO updatedEmployeeDTO) {
       Optional<Employee> existingEmployee = employeeService.getEmployeeById(id);
       if (existingEmployee.isPresent()) {
           Employee updatedEmployee = existingEmployee.get();
           employeeService.mergeEmployee(updatedEmployee, updatedEmployeeDTO);
           Employee savedEmployee = employeeService.saveEmployee(updatedEmployee);
           return new ResponseEntity<>(savedEmployee, HttpStatus.OK);
       } else {
           return new ResponseEntity<>(HttpStatus.NOT_FOUND);
       }
   }

    //@RolesAllowed({"ROLE_ADMIN"})
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
   // @RolesAllowed({"ROLE_EMPLOEE","ROLE_ADMIN"})
    @GetMapping("/{employeeId}/skills")
    public Collection<Skill> getSkillsByEmployeeId(@PathVariable UUID employeeId) {
        return employeeService.getSkillsByEmployeeId(employeeId);
    }
   // @RolesAllowed({"ROLE_ADMIN"})
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

    //@RolesAllowed({"ROLE_EMPLOEE","ROLE_ADMIN"})
    @PatchMapping("/{id}")
   public Employee updateEmployeeFields(@PathVariable UUID id, @RequestBody Map<String, Object> fields) {
       return employeeService.updateEmployeeByFields(id, fields);
   }
  //  @RolesAllowed({"ROLE_ADMIN"})
   @PutMapping("/{id}/skills")
    public Employee updateEmployeeSkill(@PathVariable UUID id, @RequestBody Map<String, List<Map<String, String>>> request) {
        List<Map<String, String>> skills = request.get("skills");
        return employeeService.updateSkills(id, skills);
    }

   // @RolesAllowed({"ROLE_ADMIN"})
    @PutMapping("/{id}/weekSchedules")
    public Employee updateEmployeeWeekSchedules(@PathVariable UUID id, @RequestBody Map<String, List<Map<String, String>>> request) {
        List<Map<String, String>> schedules = request.get("weekSchedules");
        return employeeService.updateWeekSchedules(id, schedules);
    }

    //@RolesAllowed({"ROLE_EMPLOEE","ROLE_ADMIN"})
    @PutMapping("/{id}/foreignLanguages")
    public Employee updateEmployeeForeignLanguages(@PathVariable UUID id, @RequestBody Map<String, List<Map<String, String>>> request) {
        List<Map<String, String>> languages = request.get("foreignLanguages");
        return employeeService.updateForeignLanguages(id, languages);
    }


}
