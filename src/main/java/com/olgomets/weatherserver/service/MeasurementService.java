package com.olgomets.weatherserver.service;


import com.olgomets.weatherserver.model.Measurement;

import java.util.List;
import java.util.UUID;

public interface MeasurementService {

    Measurement saveMeasurement(Measurement measurement, UUID sensorKey);

    List<Measurement> getLatestMeasurements(UUID uuid);

    List<Measurement> getAllCurrentMeasurements();

}
