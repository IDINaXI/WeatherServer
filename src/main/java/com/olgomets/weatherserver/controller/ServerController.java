package com.olgomets.weatherserver.controller;


import com.olgomets.weatherserver.model.Measurement;
import com.olgomets.weatherserver.model.Sensor;
import com.olgomets.weatherserver.repository.MeasurementRepository;

import com.olgomets.weatherserver.service.MeasurementService;
import com.olgomets.weatherserver.service.SensorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
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
        return ResponseEntity.ok().body(sensorService.getAllActiveSensors());
    }

    @GetMapping("/{key}/measurements")
    @Operation(description = "Retrieve the last 20 records of a sensor")
    @ApiResponses
    public ResponseEntity<?> getLatestMeasurements(@PathVariable("key") UUID key) {
        try {
            return ResponseEntity.ok().body(measurementService.getLatestMeasurements(key));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/measurements")
    @Operation(description = "Retrieve current information from all sensors")
    @ApiResponses
    public ResponseEntity<?> getAllCurrentMeasurements() {
        try {
            return ResponseEntity.ok().body(measurementService.getAllCurrentMeasurements());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{name}")
    @Operation(description = "Retrieve a sensor by name")
    @ApiResponses
    public Sensor findSensorByName(@PathVariable String name) {
        return sensorService.findSensorByName(name);
    }


    @PostMapping("/registration")
    @Operation(description = "Register a sensor")
    @ApiResponses
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, UUID> saveSensor(@Valid @RequestBody Sensor sensor) {
        Sensor savedSensor = sensorService.saveSensor(sensor);
        return Map.of("key", savedSensor.getUuid());
    }

    @PostMapping("/{key}/measure")
    @Operation(description = "Save data from the sensor to the server")
    @ApiResponses
    public ResponseEntity<String> addMeasurement(@PathVariable("key") String key, @Valid @RequestBody Measurement measurement) {
        if (measurement.getValue() < -100 || measurement.getValue() > 100) {
            return ResponseEntity.badRequest().body("Invalid temperature value");
        }

        measurement.setTimestamp(LocalDateTime.now());
        measurementRepository.save(measurement);

        return ResponseEntity.status(HttpStatus.CREATED).body("Successfully saved");
    }

    @PutMapping("/update_sensor")
    @Operation(description = "Update sensor data")
    @ApiResponses
    public Sensor updateSensor(@RequestBody Sensor sensor) {
        return sensorService.updateSensor(sensor);
    }

    @DeleteMapping("/delete_sensor/{name}")
    @Operation(description = "Delete sensor data by name")
    @ApiResponses
    public void deleteSensor(@PathVariable String name) {
        sensorService.deleteSensor(name);
    }

}
