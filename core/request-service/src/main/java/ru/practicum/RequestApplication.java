package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.practicum.handler.ErrorDecoderConfig;

@SpringBootApplication
@EnableFeignClients(basePackages = "ru.practicum.client")
@EnableDiscoveryClient
@ConfigurationPropertiesScan
public class RequestApplication {
    public static void main(String[] args) {
        SpringApplication.run(RequestApplication.class, args);
    }
}
