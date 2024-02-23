package com.construct.constructAthens.Employees;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.construct.constructAthens.AzureStorage.StorageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import java.util.UUID;
@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeController {
    private final ObjectMapper objectMapper;
    @Autowired
    private final EmployeeService employeeService;
    @Autowired
    private StorageService storageService;
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
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        return employee.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

   /* @PostMapping("/create")
    public ResponseEntity<Employee> addEmployee(@RequestBody Employee employee) {
        Employee savedEmployee = employeeService.saveEmployee(employee);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }*/
   @PostMapping("/create")
   public ResponseEntity<Employee> addEmployee(
           @RequestPart("employee") Employee employee,
           @RequestPart("image") MultipartFile imageFile) {

       try {
           String imageUrl = storageService.upload(imageFile);
           employee.setImageUrl(imageUrl);
           Employee savedEmployee = employeeService.saveEmployee(employee);
           return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
       } catch (IOException e) {
           return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
       }
   }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee updatedEmployee) {
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
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        if (employee.isPresent()) {
            employeeService.deleteEmployee(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    /*@PatchMapping(path = "/edit/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Boolean> updatePartially(@PathVariable(name = "id") Long id,
                                                   @RequestBody EmployeeDTO dto) throws NotYetImplementedEx, NotFoundEx {
        // skipping validations for brevity
        if (dto.getOp().equalsIgnoreCase("update")) {
            boolean result = employeeService.partialUpdate(id, dto.getKey(), dto.getValue());
            return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
        } else {
            throw new NotYetImplementedEx("NOT_YET_IMPLEMENTED");
        }
    }*/
    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody List<EmployeeDTO> employeeDTOList) {
        try {
            Optional<Employee> employee = employeeService.getEmployeeById(id);
            Employee employeePatched = applyPatchToEmployee(employeeDTOList, employee.orElse(null));
            employeeService.saveEmployee(employeePatched);
            return ResponseEntity.ok(employeePatched);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private Employee applyPatchToEmployee(List<EmployeeDTO> employeeDTOList, Employee targetEmployee) throws JsonProcessingException {
        ObjectNode targetNode = objectMapper.valueToTree(targetEmployee);

        for (EmployeeDTO employeeDTO : employeeDTOList) {
            if ("replace".equals(employeeDTO.getOp())) {
                targetNode.put(employeeDTO.getKey(), employeeDTO.getValue());
            }
            // Add more logic for other operations like "add", "remove", etc. if needed
        }

        return objectMapper.treeToValue(targetNode, Employee.class);
    }


}
