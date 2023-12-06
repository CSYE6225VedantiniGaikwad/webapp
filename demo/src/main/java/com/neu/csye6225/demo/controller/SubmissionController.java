package com.neu.csye6225.demo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.neu.csye6225.demo.entities.Submission;
import com.neu.csye6225.demo.service.SubmissionService;
import com.neu.csye6225.demo.service.ValidationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@Slf4j
public class SubmissionController {
    @Autowired
    private ValidationService validationService;
    @Autowired
    private SubmissionService submissionService;
    private static final String SUBMISSION_SCHEMA_PATH = "static/submissionSchema.json";

    @PostMapping("/demo/assignments/{id}/submission")
    public ResponseEntity<Object> submitAssignments(@RequestBody String requestBody, @PathVariable(value = "id") String id, HttpServletRequest request){
        UUID uuid = UUID.fromString(id);
        JsonNode jsonNode = validationService.jsonValidation(requestBody, SUBMISSION_SCHEMA_PATH);
        String headers = request.getHeader("Content-Length");
        int length = Integer.parseInt(headers);
        Submission submission = new Submission();
        submission.setSubmissionUrl(jsonNode.get("submissionUrl").textValue());
        submission.setSubmissionDate(LocalDateTime.now());
        submission.setSubmissionUpdated(LocalDateTime.now());
        try {
            Submission submittedAssignment = submissionService.submitAssignmentById(uuid, submission, length);
            if (submittedAssignment != null) {
                return ResponseEntity.status(201).body(submittedAssignment);
            }
        }catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.status(400).build();
    }
}
