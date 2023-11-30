package com.neu.csye6225.demo.service;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neu.csye6225.demo.auth.BasicAuth;
import com.neu.csye6225.demo.entities.Assignments;
import com.neu.csye6225.demo.entities.SNSMessage;
import com.neu.csye6225.demo.entities.Submission;
import com.neu.csye6225.demo.exceptions.AssignmentNotFoundException;
import com.neu.csye6225.demo.repositories.AssignmentsRepository;
import com.neu.csye6225.demo.repositories.SubmissionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletRequest;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class SubmissionService {
    @Autowired
    private final SubmissionRepository submissionRepository;
    @Autowired
    private final BasicAuth basicAuth;
    @Autowired
    private final AssignmentsRepository assignmentsRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Value("${sns.topic.arn}")
    String TOPIC_ARN;
    @Value("${AWS_ACCESS_KEY_ID}")
    String AWS_ACCESS_KEY_ID;
    @Value("${AWS_SECRET_ACCESS_KEY}")
    String AWS_SECRET_ACCESS_KEY;

    public SubmissionService(SubmissionRepository submissionRepository, BasicAuth basicAuth, AssignmentsRepository assignmentsRepository) {
        this.submissionRepository = submissionRepository;
        this.basicAuth = basicAuth;
        this.assignmentsRepository = assignmentsRepository;
    }

    public Submission submitAssignmentById(UUID uuid, Submission requestBody, int contentLength){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assignments assignments = assignmentsRepository.findAssignmentsById(uuid);
        if (assignments == null) {
            throw new AssignmentNotFoundException("Not Found");
        }
        if (!LocalDateTime.now().isAfter(assignments.getDeadline())) {
            if (!(assignments.getNumberOfSubmits() >= assignments.getNumberOfAttempts())) {System.out.println("number of submits "+assignments.getNumberOfSubmits());
                assignments.setNumberOfSubmits(assignments.getNumberOfSubmits() + 1);
                assignmentsRepository.save(assignments);
                Submission submission = new Submission();
                submission.setAssignmentsId(assignments.getId());
                submission.setSubmissionUrl(requestBody.getSubmissionUrl());
                submission.setSubmissionDate(requestBody.getSubmissionDate());
                submission.setSubmissionUpdated(requestBody.getSubmissionUpdated());
                submissionRepository.save(submission);

                boolean status = false;
                if (contentLength < 1){
                    status = false;
                }
                else{
                    status =true;
                }

                Regions regions = Regions.US_EAST_1;

                val snsClient = AmazonSNSClientBuilder.defaultClient();

                SNSMessage snsMessage = new SNSMessage();
                snsMessage.setSubmissionUrl(submission.getSubmissionUrl());
                snsMessage.setStatus(status ? "SUCCESS" : "FAILURE");
                snsMessage.setUserEmail(authentication.getName());
                snsMessage.setAssignmentId(String.valueOf(submission.getAssignmentsId()));
                ObjectMapper objectMapper = new ObjectMapper();
                String json = null;
                try {
                    json = objectMapper.writeValueAsString(snsMessage);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                PublishRequest publishRequest = new PublishRequest(TOPIC_ARN, json);

                snsClient.publish(publishRequest);
                return submission;
            }
        }
        return null;
    }

    public void deleteSubmissionByAssignmentId(UUID id) {
        Query query = entityManager.createQuery("delete from Submission a WHERE a.assignmentsId=:id");
        query.setParameter("id", id);
        query.executeUpdate();
    }
}
