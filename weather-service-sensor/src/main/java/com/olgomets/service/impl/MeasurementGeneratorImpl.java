package com.olgomets.service.impl;

import com.olgomets.config.impl.SensorConfigurationImpl;
import com.olgomets.model.Measurement;
import com.olgomets.model.Sensor;
import com.olgomets.service.MeasurementGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;

@Log4j2
@Service
@AllArgsConstructor
public class MeasurementGeneratorImpl implements MeasurementGenerator {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ExecutorService executorService;
    private final SensorConfigurationImpl sensorConfiguration;

    public void generateMeasurements(Sensor sensor, String serverUrl) {
        try {
            double randomValue = ThreadLocalRandom.current().nextDouble(-100, 100);
            boolean isRaining = new Random().nextBoolean();

            UUID sensorKey = sensor.getUuid();

            Measurement measurement = new Measurement();
            measurement.setRaining(isRaining);
            measurement.setTimestamp(LocalDateTime.now());
            measurement.setValue(randomValue);
            measurement.setSensor(sensor);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Measurement> requestEntity = new HttpEntity<>(measurement, headers);

            log.info("Sending measurement for sensor with key: {}", sensorKey);
            String response = restTemplate.postForObject(serverUrl.replace("{key}", sensorKey.toString()), requestEntity, String.class);
            log.info("Server response: {}", response);

            int randomInterval = ThreadLocalRandom.current().nextInt(3000, 15000);
            Thread.sleep(randomInterval);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread was interrupted: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Exception occurred while sending measurement: {}", e.getMessage());
            log.debug("Stack trace: ", e);
        }
    }


    @Scheduled(initialDelay = 10000)
    @Override
    public void sendGeneratedMeasurement() {
        try {
            String sensorsListUrl = sensorConfiguration.getServerAddress() + "/api/v1/sensors";
            String serverUrl = sensorConfiguration.getServerAddress() + "/api/v1/sensors/{key}/measure";

            log.info("Fetching list of sensors from: {}", sensorsListUrl);
            List<Sensor> sensors = Arrays.asList(restTemplate.getForObject(sensorsListUrl, Sensor[].class));

            log.info("Found {} sensors. Starting measurement generation.", sensors.size());

            for (Sensor sensor : sensors) {
                executorService.submit(() -> {
                    while (true) {
                        generateMeasurements(sensor, serverUrl);
                    }
                });
            }
        } catch (Exception e) {
            log.error("Exception occurred while fetching sensors or generating measurements: {}", e.getMessage());
            log.debug("Stack trace: ", e);
        }
    }
}
