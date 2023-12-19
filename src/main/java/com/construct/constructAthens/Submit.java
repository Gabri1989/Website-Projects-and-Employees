package com.construct.constructAthens;

import jakarta.persistence.*;

@Table(name = "submits")
@Entity
public class Submit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phone;
    private String email;
    private String company;
    private String message;
    public void setId(Long id) {
        this.id = id;}
}