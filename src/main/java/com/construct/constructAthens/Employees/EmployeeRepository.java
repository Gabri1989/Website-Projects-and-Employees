package com.construct.constructAthens.Employees;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Employee findEmployeeByUsername (String username);
    Optional<Employee> findById(UUID id);

    Employee findEmployeeById(UUID id);

    Employee getEmployeesByEmploymentDate(String date);
    @Modifying
    @Transactional
    @Query("UPDATE Employee e SET e.imageURL = :imageURL, e.cvURL = :cvURL, e.signatureURL = :signatureURL WHERE e.id = :id")
    void updateEmployeeImageURL(@Param("id") UUID id,
                                @Param("imageURL") String imageURL,
                                @Param("cvURL") String cvURL,
                                @Param("signatureURL") String signatureURL);


}