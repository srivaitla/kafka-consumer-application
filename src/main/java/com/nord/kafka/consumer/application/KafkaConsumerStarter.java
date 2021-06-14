package com.nord.kafka.consumer.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class KafkaConsumerStarter {

    public static void main(String[] args) {
        SpringApplication.run(KafkaConsumerStarter.class, args);
    }

}
