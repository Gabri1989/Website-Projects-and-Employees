package com.construct.constructAthens.security.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
public class UserInfo {

    @Id
    private UUID id;
    @JsonIgnore
    private String date_account_created;
    private String username;
    private String password;
    private String roles;
    public UserInfo() {
        this.id = UUID.randomUUID();
    }
}
