package com.neu.csye6225.demo.repositories;

import com.neu.csye6225.demo.entities.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Submission findSubmissionById(UUID id);
}
