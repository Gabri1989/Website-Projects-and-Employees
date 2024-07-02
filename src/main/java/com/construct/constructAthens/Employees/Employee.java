package com.construct.constructAthens.Employees;

import com.construct.constructAthens.Employees.Employee_dependencies.*;
import com.construct.constructAthens.Projects.Projects;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatusCode;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.*;

@AllArgsConstructor
@Data
@Tag(name = "Employee", description = "employees")
@Table(name = "employees")
@Entity
public class Employee  {

    @Id
    private UUID id;
    @ElementCollection
    Collection<Skill> skills;
    @ElementCollection
    Collection<ForeignLanguage> foreignLanguages;
    @ElementCollection
    Collection<ProjectsEmployee> projects;
    @ElementCollection
    Collection<WeekSchedule> weekSchedules;

    @Size(min = 3, max = 30)
    private String username;
    @Column(name = "fullname")
    private String fullname;
    private String imageURL;
    @Email
    private String email;
    //@Pattern(regexp = "^[0-9]{10}$")
    private String number;
    private String adress;
    private String cnp;
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$")
    private String employmentDate;
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$")
    private String birthday;
    private String nationality;
    @Min(0)
    private int kids;
    private String emergencyContact;
    private String emergencyPhone;
    private String education;
    private String motherLanguage;

    public Employee() {
        this.id = UUID.randomUUID();
    }


    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return    kids == employee.kids
                && Objects.equals(id, employee.id)
                && Objects.equals(username, employee.username)
                && Objects.equals(fullname, employee.fullname)
                && Objects.equals(imageURL, employee.imageURL)
                && Objects.equals(email, employee.email)
                && Objects.equals(number, employee.number)
                && Objects.equals(adress, employee.adress)
                && Objects.equals(cnp, employee.cnp)
                && Objects.equals(employmentDate, employee.employmentDate)
                && Objects.equals(birthday, employee.birthday)
                && Objects.equals(nationality, employee.nationality)
                && Objects.equals(emergencyContact, employee.emergencyContact)
                && Objects.equals(emergencyPhone, employee.emergencyPhone)
                && Objects.equals(education, employee.education)
                && Objects.equals(motherLanguage, employee.motherLanguage);

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, fullname, imageURL, email, number, adress, cnp, employmentDate, birthday, nationality, kids, emergencyContact, emergencyPhone, education, motherLanguage);
    }
}
