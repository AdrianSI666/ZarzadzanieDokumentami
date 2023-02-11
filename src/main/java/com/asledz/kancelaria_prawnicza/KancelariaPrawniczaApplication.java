package com.asledz.kancelaria_prawnicza;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

@SpringBootApplication
public class KancelariaPrawniczaApplication {

    public static void main(String[] args) {
        SpringApplication.run(KancelariaPrawniczaApplication.class, args);
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
