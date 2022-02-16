package com.example.taskserver;

import com.example.taskserver.Configuration.DataStaxAstraProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = "com.example.taskserver")
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class TaskServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskServerApplication.class, args);
    }

}
