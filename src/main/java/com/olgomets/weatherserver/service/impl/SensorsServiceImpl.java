package com.olgomets.weatherserver.service.impl;


import com.olgomets.weatherserver.model.Sensor;
import com.olgomets.weatherserver.repository.MeasurementRepository;
import com.olgomets.weatherserver.repository.SensorsRepository;
import com.olgomets.weatherserver.service.SensorService;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Primary
public class SensorsServiceImpl implements SensorService {

    private final SensorsRepository repository;
    private final MeasurementRepository measurementRepository;

    @Override
    public List<Sensor> findAllSensors() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Sensor saveSensor(Sensor sensor) {
        if (repository.findSensorByName(sensor.getName()) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sensor name " + sensor.getName() + " already exists");
        }

        sensor.setUuid(UUID.randomUUID());
        return repository.saveAndFlush(sensor);
    }

    @Override
    public Sensor findSensorByName(String name) {
        return repository.findSensorByName(name);
    }

    @Override
    @Transactional
    public Sensor updateSensor(Sensor sensor) {
        return repository.saveAndFlush(sensor);
    }

    @Override
    @Transactional
    public void deleteSensor(String name) {
        repository.deleteByName(name);
    }

    @Override
    public List<Sensor> getAllActiveSensors() {
        return repository.findByActiveTrue();
    }

    @Transactional
    @Override
    public void updateLastActiveTimestamp(UUID uuid) {
        Sensor sensor = repository.findSensorByUuid(uuid);
        if (sensor == null) {
            return;
        }

        sensor.setLastActiveTimestamp(LocalDateTime.now());
        repository.saveAndFlush(sensor);
    }

    @Scheduled(initialDelay = 1000)
    @Transactional
    public void checkSensorActivity() {
        List<Sensor> sensors = repository.findAll();
        LocalDateTime timeThreshold = LocalDateTime.now().minusMinutes(1);

        for (Sensor sensor : sensors) {
            boolean isActive = measurementRepository.isSensorAndTimestampAfterExist(sensor, timeThreshold);
            sensor.setActive(isActive);
            repository.saveAndFlush(sensor);
        }
    }

}
