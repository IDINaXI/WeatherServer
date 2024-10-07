package com.olgomets.weatherserver.repository;


import com.olgomets.weatherserver.model.Measurement;
import com.olgomets.weatherserver.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface MeasurementRepository
        extends JpaRepository<Measurement, Long> {

    List<Measurement> findTop20BySensorOrderByTimestampDesc(Sensor sensor);

    @Query("SELECT m FROM Measurement m WHERE m.timestamp >= :timeThreshold")
    List<Measurement> findCurrentMeasurements(LocalDateTime timeThreshold);

    @Query("SELECT COUNT(m) > 0 FROM Measurement m WHERE m.sensor = :sensor AND m.timestamp > :timeThreshold")
    boolean isSensorAndTimestampAfterExist(Sensor sensor, LocalDateTime timeThreshold);

}
