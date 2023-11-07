package com.neu.csye6225.demo.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.neu.csye6225.demo.entities.Assignments;
import com.neu.csye6225.demo.service.AssignmentsService;
import com.neu.csye6225.demo.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.timgroup.statsd.StatsDClient;
import com.timgroup.statsd.NonBlockingStatsDClient;

@RestController
@Slf4j
public class AssignmentsController {

    @Autowired
    private AssignmentsService assignmentsService;
    @Autowired
    private ValidationService validationService;
    private static final String SCHEMA_PATH = "static/schema.json";

    @Value("${env.domain:localhost}")
    private static String domain;
    private static final StatsDClient statsd = new NonBlockingStatsDClient("statsdClient", domain, 8125);

    public AssignmentsController(AssignmentsService assignmentsService, ValidationService validationService) {
        this.assignmentsService = assignmentsService;
        this.validationService = validationService;
    }

    @GetMapping("/v1/assignments")
    public ResponseEntity<Object> getAllAssignments(@RequestBody(required = false) String requestString, @RequestParam(required = false) String requestParm) {
        if (requestString != null || requestParm != null) {
            return ResponseEntity.status(400).build();
        }
        List<Assignments> assignmentsList = assignmentsService.getAllAssignments();
        statsd.incrementCounter("api.assignments.getAll");
        return ResponseEntity.ok(assignmentsList);
    }

    @PostMapping("/v1/assignments")
    public ResponseEntity<String> createAssignments (@RequestBody String s){
        try {
            JsonNode jsonNodeRequest = validationService.jsonValidation(s, SCHEMA_PATH);
            assignmentsService.creatAssignments(jsonNodeRequest);
            statsd.incrementCounter("api.assignments.create");
            return ResponseEntity.status(201).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            statsd.incrementCounter("api.assignments.create.failed");
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping("/v1/assignments/{id}")
    public ResponseEntity<Object> getAssignmentById ( @PathVariable String id ) {
        UUID uuid = UUID.fromString(id);
        Assignments assignments = assignmentsService.getAssignmentsById(uuid);
        statsd.incrementCounter("api.assignments.getById");
        return ResponseEntity.status(200).body(assignments);
    }

    @DeleteMapping("/v1/assignments/{id}")
    public ResponseEntity<Object> deleteAssignmentById ( @PathVariable String id ) {
        UUID uuid = UUID.fromString(id);
        statsd.incrementCounter("api.assignments.delete");
        return assignmentsService.deleteAssignmentsById(uuid)?
                ResponseEntity.status(204).build() : ResponseEntity.status(404).build();
    }

    @PutMapping("/v1/assignments/{id}")
    public ResponseEntity<Object> updateAssignments(@RequestBody String requestBody,
                                                    @PathVariable String id){
        JsonNode jsonNode = validationService.jsonValidation(requestBody, SCHEMA_PATH);
        UUID uuid = UUID.fromString(id);
        Assignments assignments = new Assignments();
        assignments.setName(jsonNode.get("name").textValue());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        LocalDateTime date = LocalDateTime.parse(jsonNode.get("deadline").textValue(), dateTimeFormatter);
        assignments.setDeadline(date);
        assignments.setPoints(jsonNode.get("points").intValue());
        assignments.setAssignmentUpdated(LocalDateTime.now());
        if (!assignmentsService.updateAssignmnentsById(uuid, assignments)){
            statsd.incrementCounter("api.assignments.update.failed");
            return ResponseEntity.status(404).build();
        }
        log.info("Assignment Updated in Database");
        statsd.incrementCounter("api.assignments.update");
        return ResponseEntity.status(204).build();

    }
}
