package com.olgomets.service.impl;


import com.olgomets.model.Sensor;
import com.olgomets.repository.MeasurementRepository;
import com.olgomets.repository.SensorsRepository;
import com.olgomets.service.SensorService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
@AllArgsConstructor
@Primary
public class SensorsServiceImpl implements SensorService {

    private final SensorsRepository repository;
    private final MeasurementRepository measurementRepository;

    @Override
    public List<Sensor> findAllSensors() {
        log.info("Fetching all sensors");
        return repository.findAll();
    }

    @Override
    @Transactional
    public Sensor saveSensor(Sensor sensor) {
        log.info("Attempting to save sensor with name: {}", sensor.getName());
        if (repository.findSensorByName(sensor.getName()) != null) {
            log.warn("Sensor name '{}' already exists", sensor.getName());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sensor name " + sensor.getName() + " already exists");
        }

        sensor.setUuid(UUID.randomUUID());
        Sensor savedSensor = repository.saveAndFlush(sensor);
        log.info("Successfully saved sensor with UUID: {}", savedSensor.getUuid());
        return savedSensor;
    }

    @Override
    public Sensor findSensorByName(String name) {
        log.info("Finding sensor by name: {}", name);
        return repository.findSensorByName(name);
    }

    @Override
    @Transactional
    public Sensor updateSensor(Sensor sensor) {
        log.info("Updating sensor with UUID: {}", sensor.getUuid());
        return repository.saveAndFlush(sensor);
    }

    @Override
    @Transactional
    public void deleteSensor(String name) {
        log.info("Deleting sensor with name: {}", name);
        repository.deleteByName(name);
    }

    @Override
    public List<Sensor> getAllActiveSensors() {
        log.info("Fetching all active sensors");
        return repository.findByActiveTrue();
    }

    @Transactional
    @Override
    public void updateLastActiveTimestamp(UUID uuid) {
        log.info("Updating last active timestamp for sensor with UUID: {}", uuid);
        Sensor sensor = repository.findSensorByUuid(uuid);
        if (sensor == null) {
            log.warn("Sensor with UUID: {} not found", uuid);
            return;
        }

        sensor.setLastActiveTimestamp(LocalDateTime.now());
        repository.saveAndFlush(sensor);
        log.info("Updated last active timestamp for sensor with UUID: {}", uuid);
    }

    @Scheduled(initialDelay = 1000)
    @Transactional
    public void checkSensorActivity() {
        log.info("Checking sensor activity");
        List<Sensor> sensors = repository.findAll();
        LocalDateTime timeThreshold = LocalDateTime.now().minusMinutes(1);

        for (Sensor sensor : sensors) {
            boolean isActive = measurementRepository.isSensorAndTimestampAfterExist(sensor, timeThreshold);
            sensor.setActive(isActive);
            repository.saveAndFlush(sensor);
        }
    }

}
