package com.example.taskserver;

import com.example.taskserver.Configuration.DataStaxAstraProperties;
import com.example.taskserver.Configuration.TaskClientProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication(scanBasePackages = "com.example.taskserver")
@EnableConfigurationProperties({DataStaxAstraProperties.class, TaskClientProperties.class})
public class TaskServerApplication {
    private final static Logger logger=LoggerFactory.getLogger(TaskServerApplication.class);

    public static void main(String[] args) {

        SpringApplication.run(TaskServerApplication.class, args);
    }

}
