package com.olgomets.weatherserver.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "measurements")
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Sensor sensor;

    @Min(-100)
    @Max(100)
    @Column(nullable = false)
    private double value;

    @Column(nullable = false)
    private boolean raining;

    @Column(nullable = false)
    private LocalDateTime timestamp;

}

