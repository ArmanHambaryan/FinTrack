package com.example.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import service.impl.KafkaProducerService;

@SpringBootApplication
@EnableCaching
@ComponentScan(basePackages = {"com.example", "service", "mapper"})
@EntityScan(basePackages = {"com.example", "model"})
@EnableJpaRepositories(basePackages = {"com.example", "repository"})
@EnableAsync
public class AppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

    @Bean
    CommandLineRunner testKafka(KafkaProducerService producerService) {
        return args -> {
            System.out.println("=== Սկսվում է Kafka-ի թեստավորումը ===");
            // Ուղարկում ենք փորձնական նամակ նույն թեմայով, որը լսում է Consumer-ը
            producerService.sendMessage("fintrack-test-topic", "Ողջո՜ւյն, Kafka-ն հաջողությամբ ինտեգրվեց FinTrack-ում:");
        };

    }
}
