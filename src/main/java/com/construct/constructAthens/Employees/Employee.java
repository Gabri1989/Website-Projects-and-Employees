package com.construct.constructAthens.Employees;

import jakarta.persistence.*;

@Table(name = "employees")
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String imageURL;
    private String email;
    private String number;
    private double latitude = 0.0;
    private double longitude = 0.0;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
}