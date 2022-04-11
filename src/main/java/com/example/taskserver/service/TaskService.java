package com.example.taskserver.service;

import com.example.taskserver.configuration.TaskClientProperties;
import com.example.taskserver.dto.*;
import com.example.taskserver.domain.Task;
import com.example.taskserver.repository.TaskRepository;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;


import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TaskService {
    private final TaskRepository repository;
    private final RestTemplate restTemplate;
    private final TaskClientProperties properties;
    @Value("${my.app.secret}")
    private String jwtSecret;


    public TaskService(TaskRepository repository, TaskClientProperties properties, HttpComponentsClientHttpRequestFactory factory) {
        this.repository = repository;
        this.restTemplate = new RestTemplate(factory);
        this.properties = properties;
    }

    public Task save(TaskDTO taskDTO) {
        log.info("Task before saving: {}", taskDTO);
        Task task2 = repository.findByName(taskDTO.getName());
        if (task2 != null) {
            log.error("Task in DB: {}", taskDTO);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        List<Task> list = repository.findAllTasks();
        Long number;
        try {
            number = list.stream().max((o1, o2) -> (int) (o1.getId() - o2.getId())).get().getId();
        } catch (NoSuchElementException e) {
            number = 0L;
        }
        Task task = new Task();
        task.setId(++number);
        task.setName(taskDTO.getName());
        task.setDescription(taskDTO.getDescription());
        task.setStrength(taskDTO.getStrength());
        return repository.save(task);
    }

    public Task findById(Long id) {
        Task task = repository.getTaskById(id);
        if (Objects.isNull(task)) {
            log.error("Can't found task with id: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            return task;
        }
    }

    public void delete(Long id) {
        log.info("Task for deleting: {}", repository.getTaskById(id));
        Optional<Task> optionalTask = repository.findById(id);
        if (!optionalTask.isPresent()) {
            log.error("task with id: {} not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
    }


    public Object findByName(String name) {
        Task task = repository.findByName(name);
        if (task == null) {
            log.info("Task by name: {} not found", name);
            return Collections.emptyList();
        }
        log.info("Find Task: {}. By name: {}", task, name);
        return task;
    }

    public Task update(Long id, TaskForUpdateDTO taskForUpdateDTO) {
        log.info("Task with id: {}. And body: {}", id, taskForUpdateDTO);
        Task task1 = findById(id);
        task1.setId(id);
        if (taskForUpdateDTO.getName() != null) {
            Task task = repository.findByName(taskForUpdateDTO.getName());
            if (task != null && !task1.getName().equals(taskForUpdateDTO.getName()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            task1.setName(taskForUpdateDTO.getName());
        }
        if (taskForUpdateDTO.getDescription() != null) {
            task1.setDescription(taskForUpdateDTO.getDescription());
        }
        if (taskForUpdateDTO.getStrength() != null) {
            task1.setStrength(taskForUpdateDTO.getStrength());
        }
        log.info("Task which updating: {}", task1);
        repository.update(id, task1.getName(), task1.getDescription(), task1.getStrength());
        return task1;
    }

    public void addTaskToBand(Long id, String bandName, HttpServletRequest request) {
        log.info("add to Task with id: {}. Band name: {}", id, bandName);
        ResponseEntity<Band> responseEntity;
        try {
            responseEntity = restTemplate.exchange(properties.getUrlBands() + bandName,
                    HttpMethod.GET, new HttpEntity<>(createHeaders(request.getHeader("Authorization"))), new ParameterizedTypeReference<Band>() {
                    });
        } catch (HttpClientErrorException e) {
            log.error("Client error with status code: {}", e.getStatusCode());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Band band = responseEntity.getBody();
        Long bandId = band.getId();
        repository.addTaskToBand(id, bandId);
    }

    public void makeTaskCompleted(Long id, HttpServletRequest request) {
        Optional<Task> task = repository.findById(id);
        if (!task.isPresent()) {
            log.error("Task id: {}. Not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        try {
            if (task.get().isCompleted()) {
                log.error("Task is completed: {}", task.get());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
            HttpHeaders headers = createHeaders(request.getHeader("Authorization"));
            log.info("PROPERTIES: {}, ", properties.getUrlBandsForLogic());
            Boolean object = restTemplate.exchange(properties.getUrlBandsForLogic(), HttpMethod.GET,
                    new HttpEntity<>(headers), Boolean.class, task.get().getId()).getBody();
            log.info("RESPONSE: {}", object);
            if (!object) {
                log.error("Task is not completed");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
            Map<String, List<Weapon>> mapOfWeapons =
                    restTemplate.exchange(properties.getUrlWeapons(),
                            HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<Map<String, List<Weapon>>>() {
                            }).getBody();
            List<User> mapOfUsers =
                    restTemplate.exchange(properties.getUrlUsers(),
                            HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<User>>() {
                            }).getBody();
            List<Weapon> listWeapon = mapOfWeapons.get("weapons").stream().filter(o -> o.getTaskId() != null)
                    .filter(o -> o.getTaskId().equals(id)).collect(Collectors.toList());
            List<User> listUser = mapOfUsers.stream().filter(o -> o.getTaskId() != null)
                    .filter(o -> o.getTaskId().equals(id)).collect(Collectors.toList());
            if (listUser.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            log.info("List of Users: {}", listUser);
            log.info("List of Weapons: {}", listWeapon);
            listUser.forEach(user -> {
                restTemplate.patchForObject(properties.getUrlUsers() + user.getUserId(),
                        new HttpEntity<>(Map.of("taskId", 0L), headers), Object.class);
            });
            listWeapon.forEach(weapon -> {
                restTemplate.patchForObject(properties.getUrlWeapons() + weapon.getId(),
                        new HttpEntity<>(Map.of("task_id", 0L), headers), Object.class);
            });
            repository.makeTaskCompleted(id);
        } catch (HttpClientErrorException e) {
            log.error("Client Error: {}", e.getMessage());
            throw new ResponseStatusException(e.getStatusCode());
        } catch (Exception e) {
            log.error("Another error: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public List<Task> findAllTasks() {
        return repository.findAll();
    }

    public void isTokenValidBoss(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            String[] s;
            try {
                log.error("Unauthorized with this JWT");
                s = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(headerAuth.substring(7)).getBody().getSubject().split(" ");
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }
            if (s[2].contains("ROLE_BOSS")) {
                return;
            } else {
                log.error("Forbidden with this JWT");
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        } else {
            log.error("Unauthorized with this JWT");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    public void isTokenValidBossAndUser(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            String[] s;
            try {
                s = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(headerAuth.substring(7)).getBody().getSubject().split(" ");
            } catch (Exception e) {
                log.error("Unauthorized with this JWT");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }
            if (s[2].contains("ROLE_BOSS") || s[2].contains("ROLE_USER")) {
                return;
            } else {
                log.error("Forbidden with this JWT");
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        } else {
            log.error("Unauthorized with this JWT");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }


    private HttpHeaders createHeaders(String jwt) {
        return new HttpHeaders() {{
            set("Authorization", jwt);
        }};
    }
}
