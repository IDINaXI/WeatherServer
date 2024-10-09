package com.olgomets.service;

import com.olgomets.model.Sensor;

public interface MeasurementGenerator {

    void sendGeneratedMeasurement();

    void generateMeasurements(Sensor sensor, String serverUrl);

}
