package com.construct.constructAthens.Employees;

import com.construct.constructAthens.security.entity.UserInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @NotNull
    @Size(min = 0, max = 20)
    private String username;
    @Column(name = "Nume_complet")
    private String numecomplet;
    @JsonIgnore
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
    @JsonIgnore
    private String curiculum;
    @JsonIgnore
    private String signature;
    private String education;

    public void setId(UUID id) {
        this.id = id;
    }



    //public void setImageUrl(String imageURL) {
      //  this.imageURL = imageURL;
   // }

    //public String getImageUrl() {
     //   return imageURL;
    //}
}

