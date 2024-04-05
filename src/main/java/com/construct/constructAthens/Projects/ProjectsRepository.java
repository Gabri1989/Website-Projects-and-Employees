package com.construct.constructAthens.Projects;

import com.construct.constructAthens.Employees.Employee;
import com.construct.constructAthens.Employees.EmployeeRepository;
import com.construct.constructAthens.Employees.Employee_dependencies.ProjectsEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectsRepository  extends JpaRepository<Projects, UUID> {
    default ProjectsEmployee findByEmployeeId(UUID id, EmployeeRepository employeeRepository) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee != null) {
            for (ProjectsEmployee projectsEmployee : employee.getProjects()) {
                if (projectsEmployee != null) {
                    return projectsEmployee;
                }
            }
        }
        return null;
    }
}
