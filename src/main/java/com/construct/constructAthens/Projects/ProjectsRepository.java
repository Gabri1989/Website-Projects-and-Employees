package com.construct.constructAthens.Projects;

import com.construct.constructAthens.Employees.Employee;
import com.construct.constructAthens.Employees.EmployeeRepository;
import com.construct.constructAthens.Employees.Employee_dependencies.ProjectsEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectsRepository  extends JpaRepository<Projects, UUID> {
    @Query("SELECT p FROM Projects p JOIN p.projectEmployees pe WHERE pe.employeeId = :id OR EXISTS (SELECT phs FROM p.projectHeadSites phs WHERE phs.headSiteId = :id)")
    List<Projects> findProjectsByEmployeeIdOrHeadSiteId(@Param("id") UUID id);
    boolean existsByNameProject(String nameProject);
    Optional<Projects> findByProjectId(UUID id);
}
