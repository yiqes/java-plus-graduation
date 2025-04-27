//package ru.practicum.handler;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.retry.backoff.FixedBackOffPolicy;
//import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
//import org.springframework.retry.support.RetryTemplate;
//
//@Configuration
//public class RetryConfig {
//    @Bean
//    public RetryTemplate retryTemplate() {
//        RetryTemplate retryTemplate = new RetryTemplate();
//
//        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
//        backOffPolicy.setBackOffPeriod(3000L);  // Пауза 3 секунды
//        retryTemplate.setBackOffPolicy(backOffPolicy);
//
//        MaxAttemptsRetryPolicy retryPolicy = new MaxAttemptsRetryPolicy();
//        retryPolicy.setMaxAttempts(3);  // Максимум 3 попытки
//        retryTemplate.setRetryPolicy(retryPolicy);
//
//        return retryTemplate;
//    }
//}
