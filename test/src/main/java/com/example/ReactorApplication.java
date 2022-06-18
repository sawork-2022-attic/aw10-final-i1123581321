package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

@SpringBootApplication
public class ReactorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReactorApplication.class, args);
    }

    @Bean
    public ConnectableFlux<Long> source(){
        return Flux.interval(Duration.ofSeconds(5)).publish();
    }
}
