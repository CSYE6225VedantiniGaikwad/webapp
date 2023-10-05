package com.neu.csye6225.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.neu.csye6225.demo.auth.BasicAuth;
import com.neu.csye6225.demo.entities.Assignments;
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
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class AssignmentsService {

    @Autowired
    private final AssignmentsRepository assignmentsRepository;
    @Autowired
    private final BasicAuth basicAuth;
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
        assignmentsRepository.save(assignments);
    }

    public Assignments getAssignmentsById ( UUID id ) {
        return assignmentsRepository.findAssignmentsById(id);
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
            return true;
        }
        return false;    }

    public boolean updateAssignmnentsById ( UUID uuid, Assignments requestBody ) {
        Assignments assignments = assignmentsRepository.findAssignmentsById(uuid);
        assignments.setName(requestBody.getName());
        assignments.setPoints(requestBody.getPoints());
        assignments.setNumberOfAttempts(requestBody.getNumberOfAttempts());
        return assignmentsRepository.save(assignments) != null ? true : false;
    }
}
