package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import ru.practicum.processor.EventSimilarityProcessor;
import ru.practicum.processor.UserActionEventProcessor;

@EnableDiscoveryClient
@SpringBootApplication
public class AnalyzerApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AnalyzerApplication.class, args);

        final UserActionEventProcessor userActionProcessor =
                context.getBean(UserActionEventProcessor.class);
        final EventSimilarityProcessor eventSimilarityProcessor =
                context.getBean(EventSimilarityProcessor.class);

        Thread hubEventsThread = new Thread(userActionProcessor);
        hubEventsThread.setName("UserActionHandlerThread");
        hubEventsThread.start();

        eventSimilarityProcessor.run();
    }
}