package com.construct.constructAthens.Employees;

import com.construct.constructAthens.Employees.Employee_dependencies.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatusCode;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

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
    Collection<Projects> projects;
    @ElementCollection
    Collection<WeekSchedule> weekSchedules;


    @Size(min = 1, max = 20)
    private String username;
    @Column(name = "fullname")
    private String fullname;
    private String imageURL;
    private String email;
    @Pattern(regexp = "\\+?[0-9]+")
    private String number;
    private String adress;
    private String CNP;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate employmentDate;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthday;
    private String nationality;
    private int kids;
    private String emergencyContact;
    private String emergencyPhone;
    private String education;
    private double timegps;
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
        return kids == employee.kids && Double.compare(timegps, employee.timegps) == 0 && Objects.equals(id, employee.id) && Objects.equals(username, employee.username) && Objects.equals(fullname, employee.fullname) && Objects.equals(imageURL, employee.imageURL) && Objects.equals(email, employee.email) && Objects.equals(number, employee.number) && Objects.equals(adress, employee.adress) && Objects.equals(CNP, employee.CNP) && Objects.equals(employmentDate, employee.employmentDate) && Objects.equals(birthday, employee.birthday) && Objects.equals(nationality, employee.nationality) && Objects.equals(emergencyContact, employee.emergencyContact) && Objects.equals(emergencyPhone, employee.emergencyPhone) && Objects.equals(education, employee.education) && Objects.equals(motherLanguage, employee.motherLanguage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, fullname, imageURL, email, number, adress, CNP, employmentDate, birthday, nationality, kids, emergencyContact, emergencyPhone, education, timegps, motherLanguage);
    }
}

