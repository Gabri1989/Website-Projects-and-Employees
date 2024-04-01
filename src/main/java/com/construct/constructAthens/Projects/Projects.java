package com.construct.constructAthens.Projects;

import com.construct.constructAthens.Employees.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
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
    private String headOfSite;
    private Date startData;

    private Date endData;
    private double Latitude;
    private double Longitude;
    private int radius;
    @Enumerated(EnumType.STRING)
    private ProjectStatus statusProject;
    @ElementCollection
    @CollectionTable(name = "employees_for_project", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "fullname")
    private List<String> employeeNames;
    public enum ProjectStatus {
        ON_GOING,
        FINISHED
    }
}

