package com.neu.csye6225.demo.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "assignments")
public class Assignments {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private int points;
    private int numberOfAttempts;
    @JsonFormat(pattern="YYYY-MM-DD'T'HH:MM:SS.SSS'Z'",shape = JsonFormat.Shape.STRING, locale = "en_GB")
    private LocalDateTime deadline;
    private LocalDateTime assignmentCreated;
    private LocalDateTime assignmentUpdated;
    private String ownerOfAssignment;
    private int numberOfSubmits;
}
