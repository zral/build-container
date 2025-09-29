package com.example.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;

@SpringBootApplication
public class JavaMicroserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(JavaMicroserviceApplication.class, args);
    }
}

@RestController
class HealthController {
    @GetMapping("/health")
    @Timed(value = "health.time", description = "Time spent on health endpoint")
    @Counted(value = "health.count", description = "Times health endpoint is called")
    public String health() {
        return "{\"status\":\"Healthy\"}";
    }
}
