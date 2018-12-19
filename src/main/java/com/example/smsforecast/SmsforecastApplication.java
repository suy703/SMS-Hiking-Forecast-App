package com.example.smsforecast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmsforecastApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmsforecastApplication.class, args);
    }
}
