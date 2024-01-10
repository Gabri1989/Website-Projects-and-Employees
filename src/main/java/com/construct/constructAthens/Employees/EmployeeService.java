package com.construct.constructAthens.Employees;

import com.construct.constructAthens.Employees.exception.NotFoundEx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Slf4j
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

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
    public boolean partialUpdate(Long id, String key, String value)
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

            employeeRepository.save(user);
            return true;
        } else {
            throw new NotFoundEx("RESOURCE_NOT_FOUND");
        }
    }
}