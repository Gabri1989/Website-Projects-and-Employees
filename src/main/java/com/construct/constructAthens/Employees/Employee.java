package com.construct.constructAthens.Employees;

import com.construct.constructAthens.Employees.Employee_dependencies.ForeignLanguage;
import com.construct.constructAthens.Employees.Employee_dependencies.Projects;
import com.construct.constructAthens.Employees.Employee_dependencies.Skill;
import com.construct.constructAthens.Employees.Employee_dependencies.WeekSchedule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Tag(name = "Employee", description = "employees")
@Table(name = "employees")
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ElementCollection
    Collection<Skill> skills;
    @ElementCollection
    Collection<ForeignLanguage> foreignLanguages;
    @ElementCollection
    Collection<Projects> projects;
    @ElementCollection
    Collection<WeekSchedule> weekSchedules;
    @NotNull
    @Size(min = 0, max = 20)
    private String username;
    @Column(name = "Nume_complet")
    private String numecomplet;
    private String imageURL;
    @Email
    private String email;
    @NotNull
    @Pattern(regexp = "\\+?[0-9]+")
    private String number;
    private String adress;
    private String CNP;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate employmentDate;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthday;
    private String motherLanguage;
    private String nationality;
    private int kids;

    private float timegps;
    private String emergencyContact;
    private String emergencyPhone;
    private String curiculum;
    private String signature;
    private String education;



    public void setId(UUID id) {
        this.id = id;
    }


}

