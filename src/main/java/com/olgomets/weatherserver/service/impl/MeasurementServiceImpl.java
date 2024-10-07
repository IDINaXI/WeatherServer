package com.olgomets.weatherserver.service.impl;

import com.olgomets.weatherserver.model.Measurement;
import com.olgomets.weatherserver.model.Sensor;
import com.olgomets.weatherserver.repository.MeasurementRepository;
import com.olgomets.weatherserver.repository.SensorsRepository;
import com.olgomets.weatherserver.service.MeasurementService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MeasurementServiceImpl
        implements MeasurementService {

    private final MeasurementRepository measurementRepository;
    private final SensorsRepository sensorsRepository;

    @Override
    @Transactional
    public Measurement saveMeasurement(Measurement measurement, UUID sensorUuid) {
        Sensor sensor = sensorsRepository.findSensorByUuid(sensorUuid);
        if (sensor == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor not found");
        }

        measurement.setSensor(sensor);
        measurement.setTimestamp(LocalDateTime.now());
        return measurementRepository.save(measurement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Measurement> getLatestMeasurements(UUID uuid) {
        Sensor sensor = sensorsRepository.findSensorByUuid(uuid);
        if (sensor == null) {
            throw new IllegalArgumentException("Sensor UUID '" + uuid + "' not found");
        }

        return measurementRepository.findTop20BySensorOrderByTimestampDesc(sensor);

    }

    @Override
    @Transactional(readOnly = true)
    public List<Measurement> getAllCurrentMeasurements() {
        LocalDateTime timeThreshold = LocalDateTime.now().minusMinutes(1);
        return measurementRepository.findCurrentMeasurements(timeThreshold);
    }

}

