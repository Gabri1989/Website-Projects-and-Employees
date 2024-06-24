package com.construct.constructAthens.Employees.Employee_dependencies;

import jakarta.persistence.ElementCollection;
import lombok.Data;

import java.util.Collection;
@Data
public class EmployeeDTO {
    private String motherLanguage;
    private String nationality;
    private String employmentDate;
    private String cnp;
    private String username;
    @ElementCollection
    Collection<Skill> skills;
    @ElementCollection
    Collection<ForeignLanguage> foreignLanguages;
    @ElementCollection
    Collection<WeekSchedule> weekSchedules;
}
