package com.neu.csye6225.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neu.csye6225.demo.auth.BasicAuth;
import com.neu.csye6225.demo.entities.Assignments;
import com.neu.csye6225.demo.exceptions.AssignmentNotFoundException;
import com.neu.csye6225.demo.exceptions.CannotAccessException;
import com.neu.csye6225.demo.repositories.AssignmentsRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AssignmentsService {

    @Autowired
    private final AssignmentsRepository assignmentsRepository;
    @Autowired
    private final BasicAuth basicAuth;
    @Autowired
    private SubmissionService submissionService;
    @PersistenceContext
    private EntityManager entityManager;

    public AssignmentsService( AssignmentsRepository assignmentsRepository, BasicAuth basicAuth ) {
        this.assignmentsRepository = assignmentsRepository;
        this.basicAuth = basicAuth;
    }


    public List<Assignments> getAllAssignments () {
        return assignmentsRepository.findAll();
    }

    public void creatAssignments (JsonNode jsonNode) {
        Assignments assignments = new Assignments();
        assignments.setName(jsonNode.get("name").textValue());
        assignments.setPoints(jsonNode.get("points").intValue());
        assignments.setNumberOfAttempts(jsonNode.get("num_of_attempts").intValue());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        LocalDateTime localDateTime = LocalDateTime.parse(jsonNode.get("deadline").textValue(),dateTimeFormatter);
        assignments.setDeadline(localDateTime);
        assignments.setAssignmentCreated(LocalDateTime.now());
        assignments.setAssignmentUpdated(LocalDateTime.now());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assignments.setOwnerOfAssignment(authentication.getName());
        assignments.setNumberOfSubmits(0);
        assignmentsRepository.save(assignments);
    }

    public Assignments getAssignmentsById ( UUID id ) {
        if (id == null)
            throw new IllegalArgumentException("Invalid User");
        Optional<Assignments> optionalUser = Optional.ofNullable(assignmentsRepository.findAssignmentsById(id));

        Assignments assignment = optionalUser.orElseThrow(() -> new AssignmentNotFoundException("Assignment not found"));
        if (!assignment.getOwnerOfAssignment().equals(SecurityContextHolder.getContext().getAuthentication().getName()))
            throw new CannotAccessException("Cannot access requested resource");

        return assignment;
    }

    @Transactional
    public boolean deleteAssignmentsById ( UUID id ) {
        Assignments assignments = assignmentsRepository.findAssignmentsById(id);
        if (assignments ==  null) {
            return false;
        }
        if (assignments.getOwnerOfAssignment().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
            Query query = entityManager.createQuery("delete from Assignments a WHERE a.id=:id");
            query.setParameter("id", id);
            query.executeUpdate();
            submissionService.deleteSubmissionByAssignmentId(id);
            return true;
        }
        throw new CannotAccessException("Cannot access the requested Data");
    }

    public boolean updateAssignmnentsById ( UUID uuid, Assignments requestBody ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assignments assignments = assignmentsRepository.findAssignmentsById(uuid);
        if (assignments == null){
            throw new AssignmentNotFoundException("Not Found");
        }
        if (authentication.getPrincipal().equals(assignments.getOwnerOfAssignment())) {
            assignments.setName(requestBody.getName());
            assignments.setPoints(requestBody.getPoints());
            assignments.setNumberOfAttempts(requestBody.getNumberOfAttempts());
            return assignmentsRepository.save(assignments) != null ? true : false;
        }
        else {
            throw new CannotAccessException("Cannot access the requested Data");
        }
    }
}
