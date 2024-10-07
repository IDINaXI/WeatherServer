package com.olgomets.weatherserver.service.impl;

import com.olgomets.weatherserver.config.impl.SensorConfigurationImpl;
import com.olgomets.weatherserver.model.Measurement;
import com.olgomets.weatherserver.model.Sensor;
import com.olgomets.weatherserver.service.MeasurementGenerator;
import lombok.AllArgsConstructor;
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


@Service
@AllArgsConstructor
public class MeasurementGeneratorImpl implements MeasurementGenerator {


    private final RestTemplate restTemplate = new RestTemplate();
    private final ExecutorService executorService;
    private final SensorConfigurationImpl sensorConfiguration;

    @Scheduled(fixedRate = 5000)
    @Override
    public void sendGeneratedMeasurement() {

        String sensorsListUrl = sensorConfiguration.getServerAddress() + "/api/v1/sensors";
        String serverUrl = sensorConfiguration.getServerAddress() + "/api/v1/sensors/{key}/measure";
        List<Sensor> sensors = Arrays.stream(restTemplate.getForObject(sensorsListUrl, Sensor[].class)).toList();

        for (Sensor sensor : sensors) {
            executorService.submit(() -> {
                while (true) {
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

                        String response = restTemplate.postForObject(serverUrl.replace("{key}", sensorKey.toString()), requestEntity, String.class);
                        System.out.println("Server : " + response);

                        int randomInterval = ThreadLocalRandom.current().nextInt(3000, 15000);
                        Thread.sleep(randomInterval);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        System.err.println("Exception: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
