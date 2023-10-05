package com.neu.csye6225.demo.repositories;

import com.neu.csye6225.demo.entities.Assignments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AssignmentsRepository extends JpaRepository<Assignments, Long> {
    Assignments findAssignmentsById(UUID id);

}
