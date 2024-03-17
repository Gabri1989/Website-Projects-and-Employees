package com.construct.constructAthens.Submit;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "submits")

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Submit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Size(min=3,max=30)
    private String name;
    private String phone;
    @Email
    private String email;
    private String company;
    @NotEmpty
    private String message;

}