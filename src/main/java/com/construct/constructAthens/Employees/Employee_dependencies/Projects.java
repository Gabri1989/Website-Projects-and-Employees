package com.construct.constructAthens.Employees.Employee_dependencies;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Projects {
    private String nameProject;
    @Enumerated(EnumType.STRING)
    private ProjectStatus statusProject;
    private int timpPerDate;
    private String role;
    private String headOfSite;
    private MyContribution myContribution;
    public enum ProjectStatus {
        ON_GOING,
        FINISHED
    }
    @Embeddable
    public class MyContribution {
        private LocalDate startDataContribution;
        private LocalDate endDataContribution;

        public MyContribution(LocalDate startDataContribution, LocalDate endDataContribution) {
            this.startDataContribution = startDataContribution;
            this.endDataContribution = endDataContribution;
        }

        public MyContribution() {

        }

        public LocalDate getStartDataContribution() {
            return startDataContribution;
        }

        public void setStartDataContribution(LocalDate startDataContribution) {
            this.startDataContribution = startDataContribution;
        }

        public LocalDate getEndDataContribution() {
            return endDataContribution;
        }

        public void setEndDataContribution(LocalDate endDataContribution) {
            this.endDataContribution = endDataContribution;
        }
    }
}
