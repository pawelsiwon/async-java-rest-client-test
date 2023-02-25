package org.example.runners;


import lombok.extern.slf4j.Slf4j;
import org.example.clients.ExternalServiceClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@Slf4j
public record FetchExternalClientRunner(ExecutorService executorService, ExternalServiceClient externalServiceClient, Integer howManyRequests) implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        Instant started = Instant.now();

        List<Integer> results = Collections.synchronizedList(new ArrayList<>());
        var futures = IntStream.range(0, howManyRequests)
                .mapToObj(this::createTask)
                .map(task -> CompletableFuture.supplyAsync(task, executorService))
                .map(task -> task.thenAccept(results::add))
                .toList();

        for (var f : futures) {
            f.join();
        }

        log.info("results [ {} ]", results);
        log.info("Execution time: {}", Duration.between(started, Instant.now()).get(ChronoUnit.SECONDS));
    }

    private Supplier<Integer> createTask(Integer i) {
        log.info("Created task of {}", i);
        var created = Instant.now();

        return () -> {
            var started = Instant.now();
            log.info("Started execution of {}", i);

            var status = externalServiceClient.getServiceStatus();
            var finished = Instant.now();

            log.info("Finished execution of status={}, id={}, idle={}s, execution={}s", status, i,
                    Duration.between(created, started).getSeconds(),
                    Duration.between(started, finished).getSeconds());

            return i;
        };
    }
}
