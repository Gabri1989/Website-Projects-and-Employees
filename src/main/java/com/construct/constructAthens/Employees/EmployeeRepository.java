package com.construct.constructAthens.Employees;

import com.construct.constructAthens.Employees.Employee_dependencies.ProjectsEmployee;
import com.construct.constructAthens.Employees.Employee_dependencies.Skill;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Employee findEmployeeByUsername (String username);
    Optional<Employee> findById(UUID id);
    Employee findEmployeeByFullname(String name);
    Employee findEmployeeById(UUID id);
    Employee getEmployeesByEmploymentDate(String date);
    @Modifying
    @Transactional
    @Query("UPDATE Employee e SET e.imageURL = :imageURL WHERE e.id = :id")
    void updateEmployeeImageURL(@Param("id") UUID id, @Param("imageURL") String imageURL);

}