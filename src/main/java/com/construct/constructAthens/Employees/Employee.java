package com.construct.constructAthens.Employees;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Tag(name = "Employee", description = "employees")
@Table(name = "employees")
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 0, max = 20)
    private String name;

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

    private String nationality;
    private int kids;
    private String emergencyContact;
    private String emergencyPhone;
    private String motherLanguage;
    private String foreignLanguage;
    private String skillName;
    private String level;
    private String experience;
    private String curiculum;
    private String signature;
    private String education;

    public void setId(Long id) {
        this.id = id;
    }


    public void setImageUrl(String imageURL) {
        this.imageURL = imageURL;
    }




    public String getImageUrl() {
        return imageURL;
    }
}

