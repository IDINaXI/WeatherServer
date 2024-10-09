package com.olgomets.service;


import com.olgomets.model.Measurement;

import java.util.List;
import java.util.UUID;

public interface MeasurementService {

    Measurement saveMeasurement(Measurement measurement, UUID sensorKey);

    List<Measurement> getLatestMeasurements(UUID uuid);

    List<Measurement> getAllCurrentMeasurements();

}
