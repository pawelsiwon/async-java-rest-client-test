package org.example.clients;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class ExternalServiceConfig {

    @Value("${app.config.external.service.baseUrl}")
    private String baseUrl;

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient().newBuilder()
                .connectionPool(new ConnectionPool())
                .callTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build();
    }

    @Bean
    public ExternalServiceClient externalServiceClient(OkHttpClient okHttpClient) {
        return new ExternalServiceClient(baseUrl, okHttpClient);
    }

}
