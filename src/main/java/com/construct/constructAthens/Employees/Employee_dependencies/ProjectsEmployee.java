package com.construct.constructAthens.Employees.Employee_dependencies;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ProjectsEmployee {
    private String nameProject;
    private String statusProject;
    private String role;
    @Column(columnDefinition = "varchar")
    private String headOfSite;
    private MyContribution myContribution;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectsEmployee projects = (ProjectsEmployee) o;
        return Objects.equals(nameProject, projects.nameProject) &&
                Objects.equals(statusProject, projects.statusProject) &&
                Objects.equals(role, projects.role) &&
                Objects.equals(headOfSite, projects.headOfSite) &&
                Objects.equals(myContribution, projects.myContribution);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameProject, statusProject, role, headOfSite, myContribution);
    }

}
