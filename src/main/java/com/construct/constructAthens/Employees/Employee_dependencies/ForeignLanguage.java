package com.construct.constructAthens.Employees.Employee_dependencies;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ForeignLanguage{

    private String name;
    private String level;
}