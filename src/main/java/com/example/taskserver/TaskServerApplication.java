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

//    @Bean
//    public CommandLineRunner run(RestTemplate restTemplate, TaskClientProperties properties) throws Exception {
//        return args -> {
//            Map<String,List<Weapon>> mapOfWeapons =
//                    restTemplate.exchange(properties.getUrlWeapons(),
//                            HttpMethod.GET, null, new ParameterizedTypeReference<Map<String,List<Weapon>>>() {
//                            }).getBody();
//            System.out.println(mapOfWeapons);
//            List<User> mapOfUsers =
//                    restTemplate.exchange(properties.getUrlUsers(),
//                            HttpMethod.GET, null, new ParameterizedTypeReference<List<User>>() {
//                            }).getBody();
//            System.out.println(mapOfUsers);
//            Weapon weapon=mapOfWeapons.get("weapons").stream().filter(o->o.getTask_id().equals(2L)).findFirst().get();
//            User user=mapOfUsers.stream().filter(o->o.getTaskId().equals(2L)).findFirst().get();
//            Long weaponId=weapon.getId();
//            Long userId=user.getUserId();
//            Map<String,Object> mapForWeapons=new HashMap<>();
//            mapForWeapons.put("task_id",0L);
//            Map<String,Object> mapForUsers=new HashMap<>();
//            mapForUsers.put("taskId",0L);
//            System.out.println(weapon);
//            System.out.println(user);
//            restTemplate.patchForObject(properties.getUrlWeapons()+weaponId,new HttpEntity<>(mapForWeapons),Object.class);
//            restTemplate.patchForObject(properties.getUrlUsers()+userId,new HttpEntity<>(mapForUsers),Object.class);
//            return  repository.makeTaskCompleted(id);
//        };
//    }

}
