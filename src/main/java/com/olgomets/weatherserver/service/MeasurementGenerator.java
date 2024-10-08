package com.olgomets.weatherserver.service;

import com.olgomets.weatherserver.model.Sensor;

public interface MeasurementGenerator {

    void sendGeneratedMeasurement();

    void generateMeasurements(Sensor sensor, String serverUrl);

}
