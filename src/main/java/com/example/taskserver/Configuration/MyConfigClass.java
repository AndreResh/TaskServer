package com.example.taskserver.Configuration;

import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfigClass {
    @Bean
    public CqlSessionBuilderCustomizer customizer(DataStaxAstraProperties properties) {
        return cqlSessionBuilder -> {
            cqlSessionBuilder.withCloudSecureConnectBundle(properties.getSecureConnectBundle().toPath());
        };
    }
}
