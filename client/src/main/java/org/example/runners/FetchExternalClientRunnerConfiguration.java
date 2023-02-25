package org.example.runners;

import lombok.extern.slf4j.Slf4j;
import org.example.clients.ExternalServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
    @ConditionalOnProperty(prefix = "app.config", name = "executor", havingValue = "stealing", matchIfMissing = true)
    public ExecutorService workStealingPool() {
        log.info("Initialization of WorkStealingPool: threads={}, availableCPUs={}", threads, Runtime.getRuntime().availableProcessors());
        return Executors.newWorkStealingPool(threads);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.config", name = "executor", havingValue = "fixed")
    public ExecutorService fixedThreadPool() {
        log.info("Initialization of FixedThreadPool: availableCPUs={}", Runtime.getRuntime().availableProcessors());
        return Executors.newFixedThreadPool(threads);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.config", name = "executor", havingValue = "cached")
    public ExecutorService cachedThreadPool() {
        log.info("Initialization of VirtualThreadPerTaskExecutor: availableCPUs={}", Runtime.getRuntime().availableProcessors());
        return Executors.newCachedThreadPool();
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.config", name = "executor", havingValue = "virtual")
    public ExecutorService virtualThreadExecutor() {
        log.info("Initialization of VirtualThreadPerTaskExecutor: availableCPUs={}", Runtime.getRuntime().availableProcessors());
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
