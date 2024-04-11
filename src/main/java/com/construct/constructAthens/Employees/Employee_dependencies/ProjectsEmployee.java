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
    private int timpPerDate;
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

    @Embeddable
    public static class MyContribution {
        private String startDataContribution;
        private String endDataContribution;

        public MyContribution(String startDataContribution, String endDataContribution) {
            this.startDataContribution = startDataContribution;
            this.endDataContribution = endDataContribution;
        }

        public MyContribution() {
        }

        public String getStartDataContribution() {
            return startDataContribution;
        }

        public void setStartDataContribution(String startDataContribution) {
            this.startDataContribution = startDataContribution;
        }

        public String getEndDataContribution() {
            return endDataContribution;
        }

        public void setEndDataContribution(String endDataContribution) {
            this.endDataContribution = endDataContribution;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MyContribution that = (MyContribution) o;
            return Objects.equals(startDataContribution, that.startDataContribution) &&
                    Objects.equals(endDataContribution, that.endDataContribution);
        }

        @Override
        public int hashCode() {
            return Objects.hash(startDataContribution, endDataContribution);
        }
    }
}
