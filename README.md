# Weather Sensor API

This project is an API for managing sensors and measurements in a weather monitoring application.

## Table of Contents
- [Description](#description)
- [Technologies](#technologies)
- [Requirements](#requirements)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [API Documentation](#api-documentation)
- [Development Time](#development-time)

# Description

The Weather Sensor API allows you to register sensors, collect weather data from these sensors, and view the collected data. The API also supports simulating sensor data for testing purposes.

# Technologies

- Java 17
- Spring Boot 3.3.1
- Spring Data JPA
- Hibernate
- PostgreSQL
- Lombok
- Swagger
- SpringDoc OpenAPI

# Requirements

- JDK 17 or higher
- Maven 3.6.0 or higher
- PostgreSQL 10 or higher

# Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/IDINaXI/WeatherServer
   ```

2. Navigate to the project directory:
   ```bash
   cd wetherserver
   ```

4. Install dependencies and build the project using Maven:
   ```bash
   mvn clean install
   ```

# Running the Application

1. Start PostgreSQL and create the database:
   ```sql
   CREATE DATABASE Weather_Server;
   ```

2. Update the `application.properties` file in `src/main/resources` with your database settings:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/Weather_Server
   spring.datasource.username=yourusername
   spring.datasource.password=yourpassword
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

# API Endpoints

List of active sensors  
**GET** `/api/v1/sensors`  
Returns a list of all active sensors.

Latest 20 measurements for a sensor  
**GET** `/api/v1/sensors/{key}/measurements`  
Returns the latest 20 records for the specified sensor by its key.

All current measurements  
**GET** `/api/v1/sensors/measurements`  
Returns the latest data from all sensors.

Find sensor by name  
**GET** `/api/v1/sensors/{name}`  
Returns the sensor by its name.

Start data simulation  
**POST** `/api/v1/sensors/start`  
Starts simulating data coming from sensors.

Register a sensor  
**POST** `/api/v1/sensors/registration`  
Registers a new sensor.

Save sensor data  
**POST** `/api/v1/sensors/{key}/measure`  
Saves sensor data to the server.

Update sensor data  
**PUT** `/api/v1/sensors/update_sensor`  
Updates sensor data.

Delete a sensor by name  
**DELETE** `/api/v1/sensors/delete_sensor/{name}`  
Deletes sensor data by its name.

## API Documentation

API documentation is available at the following URL after the application is running:

```
http://localhost:8080/swagger-ui/index.html
```

## Development Time

Design - 20 minutes

Development - 5 hours

Testing - 30 minutes

Documentation - 1 hour
