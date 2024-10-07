package com.olgomets.weatherserver.repository;


import com.olgomets.weatherserver.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SensorsRepository
        extends JpaRepository<Sensor, Long> {

    Sensor findSensorByName(String name);

    Sensor findSensorByUuid(UUID uuid);

    List<Sensor> findByActiveTrue();

    void deleteByName(String name);

}


