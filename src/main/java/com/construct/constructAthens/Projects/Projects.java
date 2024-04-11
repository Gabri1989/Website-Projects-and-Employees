package com.construct.constructAthens.Projects;

import com.construct.constructAthens.Employees.Employee_dependencies.ProjectsEmployee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Projects {
    @Id
    private UUID projectId;
    private String nameProject;

    private String startData;

    private String endData;
    private double Latitude;
    private double Longitude;
    private double radius;

    private String statusProject;
    @ElementCollection
    @CollectionTable(name = "employees_for_project", joinColumns = @JoinColumn(name = "project_id"))
    @AttributeOverride(name = "employeeDetails", column = @Column(name = "employee_details"))
    private List<ProjectEmployees> projectEmployees;

    @ElementCollection
    @CollectionTable(name = "headsite_for_project", joinColumns = @JoinColumn(name = "project_id"))
    @AttributeOverride(name = "headSiteDetails", column = @Column(name = "head_site_details"))
    private List<ProjectHeadSite> projectHeadSites;

}

