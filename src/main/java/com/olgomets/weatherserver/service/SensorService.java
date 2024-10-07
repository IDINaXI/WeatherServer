package com.olgomets.weatherserver.service;


import com.olgomets.weatherserver.model.Sensor;

import java.util.List;
import java.util.UUID;

public interface SensorService {

    List<Sensor> findAllSensors();

    Sensor saveSensor(Sensor sensor);

    Sensor findSensorByName(String name);

    Sensor updateSensor(Sensor sensor);

    void deleteSensor(String name);

    List<Sensor> getAllActiveSensors();

    void updateLastActiveTimestamp(UUID uuid);

}
