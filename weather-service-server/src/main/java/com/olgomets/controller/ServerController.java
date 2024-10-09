package com.olgomets.controller;

import com.olgomets.model.Measurement;
import com.olgomets.model.Sensor;
import com.olgomets.repository.MeasurementRepository;
import com.olgomets.service.MeasurementService;
import com.olgomets.service.SensorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;


@RestController
@Log4j2
@RequestMapping("/api/v1/sensors")
@AllArgsConstructor
public class ServerController {

    private final SensorService sensorService;
    private final MeasurementService measurementService;

    @Autowired
    private final MeasurementRepository measurementRepository;

    @GetMapping
    @Operation(description = "Retrieve all active sensors")
    @ApiResponses
    public ResponseEntity<?> getAllActiveSensors() {
        log.info("Fetching all active sensors");
        return ResponseEntity.ok().body(sensorService.getAllActiveSensors());
    }

    @GetMapping("/{key}/measurements")
    @Operation(description = "Retrieve the last 20 records of a sensor")
    @ApiResponses
    public ResponseEntity<?> getLatestMeasurements(@PathVariable("key") UUID key) {
        log.info("Fetching latest measurements for sensor with key: {}", key);
        try {
            return ResponseEntity.ok().body(measurementService.getLatestMeasurements(key));
        } catch (IllegalArgumentException e) {
            log.error("Sensor not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/measurements")
    @Operation(description = "Retrieve current information from all sensors")
    @ApiResponses
    public ResponseEntity<?> getAllCurrentMeasurements() {
        log.info("Fetching current measurements from all sensors");
        try {
            return ResponseEntity.ok().body(measurementService.getAllCurrentMeasurements());
        } catch (Exception e) {
            log.error("Error retrieving measurements: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{name}")
    @Operation(description = "Retrieve a sensor by name")
    @ApiResponses
    public Sensor findSensorByName(@PathVariable String name) {
        log.info("Searching for sensor with name: {}", name);
        return sensorService.findSensorByName(name);
    }

    @PostMapping("/registration")
    @Operation(description = "Register a sensor")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses
    public Map<String, UUID> saveSensor(@Valid @RequestBody Sensor sensor) {
        log.info("Registering a new sensor: {}", sensor.getName());
        Sensor savedSensor = sensorService.saveSensor(sensor);

        return Map.of("key", savedSensor.getUuid());
    }

    @PostMapping("/{key}/measure")
    @Operation(description = "Save data from the sensor to the server")
    @ApiResponses
    public ResponseEntity<String> addMeasurement(@PathVariable("key") String key, @Valid @RequestBody Measurement measurement) {
        log.info("Saving measurement for sensor with key: {}", key);
        if (measurement.getValue() < -100 || measurement.getValue() > 100) {
            log.warn("Invalid temperature value: {}", measurement.getValue());
            return ResponseEntity.badRequest().body("Invalid temperature value");
        }

        measurement.setTimestamp(LocalDateTime.now());
        measurementRepository.save(measurement);
        log.info("Measurement successfully saved");

        return ResponseEntity.status(HttpStatus.CREATED).body("Successfully saved");
    }

    @PutMapping("/update_sensor")
    @Operation(description = "Update sensor data")
    @ApiResponses
    public Sensor updateSensor(@RequestBody Sensor sensor) {
        log.info("Updating sensor: {}", sensor.getName());
        return sensorService.updateSensor(sensor);
    }

    @DeleteMapping("/delete_sensor/{name}")
    @Operation(description = "Delete sensor data by name")
    @ApiResponses
    public void deleteSensor(@PathVariable String name) {
        log.info("Deleting sensor with name: {}", name);
        sensorService.deleteSensor(name);
    }

}
