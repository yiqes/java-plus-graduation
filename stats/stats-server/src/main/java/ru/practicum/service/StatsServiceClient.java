package ru.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import ru.practicum.exception.StatsServerUnavailable;

import java.net.URI;

@Service
public class StatsServiceClient {
    private final DiscoveryClient discoveryClient;
    private final RetryTemplate retryTemplate;
    private static final String STATS_SERVICE_ID = "stats-server";

    @Autowired
    public StatsServiceClient(DiscoveryClient discoveryClient, RetryTemplate retryTemplate) {
        this.discoveryClient = discoveryClient;
        this.retryTemplate = retryTemplate;
    }

    private ServiceInstance getInstance() {
        try {
            return discoveryClient
                    .getInstances(STATS_SERVICE_ID)
                    .getFirst();
        } catch (Exception exception) {
            throw new StatsServerUnavailable(
                    "Ошибка обнаружения адреса сервиса статистики с id: " + STATS_SERVICE_ID,
                    exception
            );
        }
    }

    private URI makeUri(String path) {
        ServiceInstance instance = retryTemplate.execute(cxt -> getInstance());
        return URI.create("http://" + instance.getHost() + ":" + instance.getPort() + path);
    }
}
