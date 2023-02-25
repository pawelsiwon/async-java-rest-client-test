package org.example.runners;


import lombok.extern.slf4j.Slf4j;
import org.example.clients.ExternalServiceClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;

@Slf4j
public record FetchExternalClientRunner(ExecutorService executorService, ExternalServiceClient externalServiceClient, Integer howManyRequests) implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Instant started = Instant.now();

        var futures = IntStream.range(0, howManyRequests)
                .mapToObj(this::createTask)
                .map(executorService::submit)
                .toList();

        List<Integer> results = new ArrayList<>(futures.size());
        for(var f : futures) {
            results.add(f.get());
        }

        log.info("results [ {} ]", results);
        log.info("Execution time: {}", Duration.between(started, Instant.now()).get(ChronoUnit.SECONDS));
    }

    private Callable<Integer> createTask(Integer i) {
        log.info("Created task of {}", i);
        var created = Instant.now();

        return () -> {
            var started = Instant.now();
            log.info("Started execution of {}", i);

            var status = externalServiceClient.getServiceStatus();
            var finished = Instant.now();

            log.info("Finished execution of status={}, id={}, idle={}, execution={}", status, i,
                    Duration.between(created, started).getSeconds(),
                    Duration.between(started, finished).getSeconds());

            return i;
        };
    }
}
