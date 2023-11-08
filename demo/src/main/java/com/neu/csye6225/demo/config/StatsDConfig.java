package com.neu.csye6225.demo.config;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StatsDConfig {
    @Value("${env.domain}")
    private String statsdHost;

    @Value("${env.port}")
    private int statsdPort;

    @Bean
    public StatsDClient statsDClient() {
        return new NonBlockingStatsDClient("webapp", statsdHost, statsdPort);
    }
}