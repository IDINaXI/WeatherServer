package com.olgomets.weatherserver.service.impl;

import com.olgomets.weatherserver.model.Measurement;
import com.olgomets.weatherserver.model.Sensor;
import com.olgomets.weatherserver.repository.MeasurementRepository;
import com.olgomets.weatherserver.repository.SensorsRepository;
import com.olgomets.weatherserver.service.MeasurementService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
@AllArgsConstructor
public class MeasurementServiceImpl
        implements MeasurementService {

    private final MeasurementRepository measurementRepository;
    private final SensorsRepository sensorsRepository;

    @Override
    @Transactional
    public Measurement saveMeasurement(Measurement measurement, UUID uuid) {
        log.info("Attempting to save measurement for sensor with UUID: {}", uuid);
        Sensor sensor = sensorsRepository.findSensorByUuid(uuid);
        if (sensor == null) {
            log.warn("Sensor with UUID: {} not found", uuid);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor not found");
        }

        measurement.setSensor(sensor);
        measurement.setTimestamp(LocalDateTime.now());
        log.info("Successfully saved measurement");
        return measurementRepository.save(measurement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Measurement> getLatestMeasurements(UUID uuid) {
        log.info("Fetching latest measurements for sensor with UUID: {}", uuid);
        Sensor sensor = sensorsRepository.findSensorByUuid(uuid);
        if (sensor == null) {
            log.warn("Sensor with UUID: {} not found", uuid);
            throw new IllegalArgumentException("Sensor UUID '" + uuid + "' not found");
        }

        List<Measurement> measurements = measurementRepository.findTop20BySensorOrderByTimestampDesc(sensor);
        log.info("Found {} measurements for sensor with UUID: {}", measurements.size(), uuid);
        return measurements;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Measurement> getAllCurrentMeasurements() {
        log.info("Fetching all current measurements from the last minute");
        LocalDateTime timeThreshold = LocalDateTime.now().minusMinutes(1);
        List<Measurement> currentMeasurements = measurementRepository.findCurrentMeasurements(timeThreshold);
        log.info("Found {} current measurements", currentMeasurements.size());
        return currentMeasurements;
    }

}

