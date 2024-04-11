package com.construct.constructAthens.Projects;

import com.construct.constructAthens.Employees.Employee;
import com.construct.constructAthens.Employees.EmployeeRepository;
import com.construct.constructAthens.Employees.Employee_dependencies.ProjectsEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProjectsRepository  extends JpaRepository<Projects, UUID> {

    Optional<Projects> findByProjectId(UUID id);
}
