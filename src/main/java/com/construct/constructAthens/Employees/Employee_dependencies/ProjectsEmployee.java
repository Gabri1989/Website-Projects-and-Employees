package com.construct.constructAthens.Employees.Employee_dependencies;

import com.construct.constructAthens.Projects.Projects;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ProjectsEmployee {
    private String nameProject;
    private String statusProject;
    private double timpPerDate;
    private String role;
    @Column(columnDefinition = "varchar")
    private String headOfSite;
    private MyContribution myContribution;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectsEmployee projects = (ProjectsEmployee) o;
        return timpPerDate == projects.timpPerDate &&
                Objects.equals(nameProject, projects.nameProject) &&
                Objects.equals(statusProject, projects.statusProject) &&
                Objects.equals(role, projects.role) &&
                Objects.equals(headOfSite, projects.headOfSite) &&
                Objects.equals(myContribution, projects.myContribution);
    }

    @Override
    public int hashCode() {
        //return Objects.hash( timpPerDate, role, myContribution);
        return Objects.hash(nameProject, statusProject, timpPerDate, role, headOfSite, myContribution);
    }

}
