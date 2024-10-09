package com.olgomets.config.impl;

import com.olgomets.config.SensorConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class SensorConfigurationImpl
        implements SensorConfiguration {

    @Value("${server.url}")
    private String serverAddress;

    @Bean(name = "commonExecutorService")
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(10);
    }

    @Override
    public String getServerAddress() {
        return serverAddress;
    }

}
