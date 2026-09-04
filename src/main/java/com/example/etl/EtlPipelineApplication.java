package com.example.etl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EtlPipelineApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtlPipelineApplication.class, args);
    }
}
