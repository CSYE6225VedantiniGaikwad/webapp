package com.neu.csye6225.demo.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "submission")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID assignmentsId;
    private String submissionUrl;
    private LocalDateTime submissionDate;
    private LocalDateTime submissionUpdated;
}
