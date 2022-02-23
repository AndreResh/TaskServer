package com.example.taskserver.Configuration;

import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

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
