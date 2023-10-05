package com.neu.csye6225.demo.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class Users {

    @Id
    @GeneratedValue( strategy = GenerationType.UUID )
    private UUID id;
    private String firstName;
    private String lastName;
    private String emailID;
    private String password;
    private LocalDateTime accountCreated;
    private LocalDateTime accountUpdated;
}
