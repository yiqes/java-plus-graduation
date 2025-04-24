package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.practicum.handler.ErrorDecoderConfig;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"ru.practicum.client", "ru.practicum"})
@ConfigurationPropertiesScan
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
