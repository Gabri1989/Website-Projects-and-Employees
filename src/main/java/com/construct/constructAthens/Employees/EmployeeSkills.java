package com.construct.constructAthens.Employees;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmployeeSkills {
    private String skillName;
    private String level;
    private String experience;
}
