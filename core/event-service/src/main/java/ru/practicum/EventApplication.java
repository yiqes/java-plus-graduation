package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import ru.practicum.handler.ErrorDecoderConfig;

@SpringBootApplication
@EnableDiscoveryClient
@ConfigurationPropertiesScan
@EnableFeignClients(basePackages = {"ru.practicum.client", "ru.practicum"})
@ComponentScan(basePackages = {"ru.practicum", "ru.practicum"})
public class EventApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventApplication.class, args);
    }
}
