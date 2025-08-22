package com.example.substracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SubsTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubsTrackerApplication.class, args);
    }

}
