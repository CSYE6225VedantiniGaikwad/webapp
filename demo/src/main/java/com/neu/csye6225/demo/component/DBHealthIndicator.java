package com.neu.csye6225.demo.component;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;

@Component
public class DBHealthIndicator implements HealthIndicator {
    private final DataSource dataSource;
    public DBHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        boolean serviceHealth = isServiceHealthy();
        if (serviceHealth) {
            return Health.up().build();
        } else {
            return Health.down().build();
        }
    }

    private boolean isServiceHealthy() {
        try {
            return dataSource.getConnection() != null;
        } catch (SQLException exception) {
            return false;
        }
    }
}
