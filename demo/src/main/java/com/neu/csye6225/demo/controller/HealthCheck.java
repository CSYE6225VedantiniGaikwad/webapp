package com.neu.csye6225.demo.controller;

import com.neu.csye6225.demo.service.HealthCheckService;
import com.timgroup.statsd.StatsDClient;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheck {

    private final HealthCheckService healthCheckService;
    @Autowired
    private final HikariDataSource hikariDataSource;

    @Autowired
    StatsDClient statsDClient;

    public HealthCheck (HealthCheckService healthCheckService, HikariDataSource hikariDataSource) {
        this.healthCheckService = healthCheckService;
        this.hikariDataSource = hikariDataSource;
    }

    @GetMapping("healthz")
    public ResponseEntity<String> getHealthCheck() {
        try {
            if (healthCheckService.dbConnectionCheck()) {
                statsDClient.increment("api.healthCheck.ok");
                return ResponseEntity.ok().build();
            }
            statsDClient.increment("api.healthCheck.failed");
            return ResponseEntity.status(503).build();
        } catch (Exception exception) {
            statsDClient.increment("api.healthCheck.failed");
            return ResponseEntity.status(503).build();
        }
    }
}
