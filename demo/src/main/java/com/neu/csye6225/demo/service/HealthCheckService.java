package com.neu.csye6225.demo.service;

import com.neu.csye6225.demo.component.DBHealthIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Service;

@Service
public class HealthCheckService {
    @Autowired
    private DBHealthIndicator dbHealthIndicator;

    public boolean dbConnectionCheck() {
        Health health = dbHealthIndicator.getHealth(true);
        Status status = health.getStatus();
        return status.equals(Status.UP);
    }
}
