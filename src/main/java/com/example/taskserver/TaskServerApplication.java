package com.example.taskserver;

import com.example.taskserver.Configuration.DataStaxAstraProperties;
import com.example.taskserver.Configuration.TaskClientProperties;
import com.example.taskserver.domain.User;
import com.example.taskserver.domain.Weapon;
import org.codehaus.jackson.JsonNode;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@SpringBootApplication(scanBasePackages = "com.example.taskserver")
@EnableConfigurationProperties({DataStaxAstraProperties.class, TaskClientProperties.class})
public class TaskServerApplication {
    private final static Logger logger=LoggerFactory.getLogger(TaskServerApplication.class);

    public static void main(String[] args) {

        SpringApplication.run(TaskServerApplication.class, args);
    }

}
