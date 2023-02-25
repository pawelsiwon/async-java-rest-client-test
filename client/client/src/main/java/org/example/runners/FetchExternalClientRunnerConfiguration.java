package org.example.runners;

import lombok.extern.slf4j.Slf4j;
import org.example.clients.ExternalServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Configuration
public class FetchExternalClientRunnerConfiguration {

    @Value("${app.config.parallel.threads:5}")
    private int threads;

    @Value("${app.config.requests.number:5}")
    private int numberOfRequests;

    @Bean
    public FetchExternalClientRunner fetchExternalClientRunner(ExecutorService executor, ExternalServiceClient client) {
        return new FetchExternalClientRunner(executor, client, numberOfRequests);
    }

    @Bean
    public ExecutorService executor() {
        log.info("Initialization of ExecutorService: threads={}, available={}", threads, Runtime.getRuntime().availableProcessors());
        return Executors.newWorkStealingPool(threads);
    }
}
