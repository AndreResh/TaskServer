package com.example.taskserver.configuration;

import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@Configuration
public class MyConfigClass {
    @Bean
    public CqlSessionBuilderCustomizer customizer(DataStaxAstraProperties properties) {
        return cqlSessionBuilder -> {
            cqlSessionBuilder.withCloudSecureConnectBundle(properties.getSecureConnectBundle().toPath());
        };
    }
    @Bean
    public HttpComponentsClientHttpRequestFactory factory(){
        return new HttpComponentsClientHttpRequestFactory();
    }
}