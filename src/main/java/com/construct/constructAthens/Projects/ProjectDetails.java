package com.construct.constructAthens.Projects;

import com.construct.constructAthens.Employees.Employee_dependencies.EmployeeCheckInOut;
import lombok.Data;

import java.util.List;
@Data
public class ProjectDetails {
    private Projects project;
    private List<EmployeeCheckInOut> employeeTimes;

    public ProjectDetails(Projects project, List<EmployeeCheckInOut> employeeTimes) {
        this.project = project;
        this.employeeTimes = employeeTimes;
    }


}