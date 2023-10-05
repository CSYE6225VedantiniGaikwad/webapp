package com.neu.csye6225.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neu.csye6225.demo.controller.AssignmentsController;
import com.networknt.schema.*;
import com.neu.csye6225.demo.exceptions.JsonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Service
@Slf4j
public class ValidationService {
    public JsonNode jsonValidation ( String json, String schemaPath) {
        JsonNode jsonNode = null;
        try (InputStream inputStream = AssignmentsController.class.getClassLoader().getResourceAsStream(schemaPath)) {

            JsonSchema jsonSchema = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7).getSchema(inputStream);

            ObjectMapper objectMapper = new ObjectMapper();
            jsonNode = objectMapper.readTree(json);

            Set<ValidationMessage> errors = jsonSchema.validate(jsonNode);
            StringBuilder combinedErrors = new StringBuilder();

            for(ValidationMessage e : errors) {
                log.error("Validation error occurred: {}", e);
                combinedErrors.append(e.toString()).append("\n");
            }
            if (errors.size() > 0)
                throw new JsonException("Improper json format: \n" + combinedErrors);

        } catch ( IOException e) {
            throw new JsonSchemaException("Error occurred while fetching the schema from resource");
        }
        return jsonNode;
    }
}
