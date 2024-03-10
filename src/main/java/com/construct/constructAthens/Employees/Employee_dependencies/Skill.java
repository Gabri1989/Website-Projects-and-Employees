package com.construct.constructAthens.Employees.Employee_dependencies;

import jakarta.persistence.*;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Skill {

    private String experience;
    private String skillName;
    private String level;
}