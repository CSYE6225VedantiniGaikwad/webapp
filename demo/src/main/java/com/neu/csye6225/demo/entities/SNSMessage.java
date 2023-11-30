package com.neu.csye6225.demo.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SNSMessage {
    @JsonProperty("submissionUrl")
    private String submissionUrl;
    @JsonProperty("status")
    private String status;
    @JsonProperty("userEmail")
    private String userEmail;
    @JsonProperty("assignmentId")
    private String assignmentId;
}
