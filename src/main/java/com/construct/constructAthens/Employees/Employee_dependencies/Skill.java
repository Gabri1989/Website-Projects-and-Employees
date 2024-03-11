package com.construct.constructAthens.Employees.Employee_dependencies;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Skill {

    private String experience;
    private String skillName;
    private String level;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Skill skill = (Skill) o;
        return Objects.equals(experience, skill.experience) &&
                Objects.equals(skillName, skill.skillName) &&
                Objects.equals(level, skill.level);
    }

    @Override
    public int hashCode() {
        return Objects.hash(experience, skillName, level);
    }
}