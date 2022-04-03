package com.example.taskserver.service;

import com.example.taskserver.Configuration.TaskClientProperties;
import com.example.taskserver.dto.Band;
import com.example.taskserver.domain.Task;
import com.example.taskserver.dto.User;
import com.example.taskserver.dto.Weapon;
import com.example.taskserver.exeption.ApiRequestExceptions;
import com.example.taskserver.repository.TaskRepository;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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

    public Task save(Task task) {
        log.info("Task before saving: {}", task);
        List<Task> list = findAll();
        Long number;
        try {
            number = list.stream().max((o1, o2) -> (int) (o1.getId() - o2.getId())).get().getId();
        } catch (NoSuchElementException e) {
            number = 0L;
        }
        task.setId(++number);
        return repository.save(task);
    }

    public Task findById(Long id) {
        log.info("Find Task by id: {}", repository.getTaskById(id));
        return repository.getTaskById(id);
    }

    public void delete(Long id) {
        log.info("Task for deleting: {}", repository.getTaskById(id));
        repository.deleteById(id);
    }

    public List<Task> findAll() {
        log.info("All tasks: {}", repository.findAllTasks());
        return repository.findAllTasks();
    }

    public Task findByName(String name) {
        log.info("Find Task by name: {}", repository.findByName(name));
        return repository.findByName(name);
    }

    public Task update(Long id, Task t) {
        log.info("Task with id: {}. And body: {}", id, t);
        Task task1 = findById(id);
        task1.setId(id);
        if (t.getName() != null) {
            task1.setName(t.getName());
        }
        if (t.getDescription() != null) {
            task1.setDescription(t.getDescription());
        }
        if (t.getStrength() != null) {
            task1.setStrength(t.getStrength());
        }
        if (t.getNumberOfPeople() != null) {
            task1.setNumberOfPeople(t.getNumberOfPeople());
        }
        if (t.getBandId() != null) {
            task1.setBandId(t.getBandId());
        }
        log.info("Task which updating: {}", task1);
        repository.update(id, task1.getName(), task1.getDescription(), task1.getStrength(), task1.getNumberOfPeople());
        return task1;
    }

    public void addTaskToBand(Long id, Band band, HttpServletRequest request) {
        log.info("add to Task with id: {}. Band name: {}", id, band.getBandName());
        band = restTemplate.exchange(properties.getUrlBands() + band.getBandName(),
                HttpMethod.GET, new HttpEntity<>(createHeaders(request.getHeader("Authorization"))), new ParameterizedTypeReference<Band>() {
                }).getBody();
        if (band != null) {
            Long bandId = band.getId();
            repository.addTaskToBand(id, bandId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

    }

    public void makeTaskCompleted(Long id, HttpServletRequest request) {
        HttpHeaders headers=createHeaders(request.getHeader("Authorization"));
        Map<String, List<Weapon>> mapOfWeapons =
                restTemplate.exchange(properties.getUrlWeapons(),
                        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<Map<String, List<Weapon>>>() {
                        }).getBody();
        List<User> mapOfUsers =
                restTemplate.exchange(properties.getUrlUsers(),
                        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<User>>() {
                        }).getBody();
        List<Weapon> listWeapon = mapOfWeapons.get("weapons").stream().filter(o->o.getTask_id()!=null).filter(o -> o.getTask_id().equals(id)).collect(Collectors.toList());
        List<User> listUser = mapOfUsers.stream().filter(o->o.getTaskId()!=null).filter(o -> o.getTaskId().equals(id)).collect(Collectors.toList());
        if (listUser.isEmpty() || listWeapon.isEmpty()) {
            throw new ApiRequestExceptions("No users or weapons with such task id");
        }
        log.info("List of Users: {}", listUser);
        log.info("List of Weapons: {}", listWeapon);
        Map<String, Long> mapForWeapons = Map.of("task_id", 0L);
        Map<String, Object> mapForUsers = Map.of("taskId", 0L);
        listUser.forEach(user -> {
            restTemplate.patchForObject(properties.getUrlUsers() + user.getUserId(), new HttpEntity<>(mapForUsers, headers), Object.class);
        });
        listWeapon.forEach(weapon -> {
            restTemplate.patchForObject(properties.getUrlWeapons() + weapon.getId(), new HttpEntity<>(mapForWeapons, headers), Object.class);
        });
        repository.makeTaskCompleted(id);
    }

    public List<Task> findAllTasks() {
        return repository.findAll();
    }

    public boolean isTokenValidBoss(HttpServletRequest request) {
        try {
            String headerAuth = request.getHeader("Authorization");
            if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
                String[] s = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(headerAuth.substring(7)).getBody().getSubject().split(" ");
                return s[2].contains("ROLE_BOSS");
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenValidBossAndUser(HttpServletRequest request) {
        try {
            String headerAuth = request.getHeader("Authorization");
            if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
                String[] s = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(headerAuth.substring(7)).getBody().getSubject().split(" ");
                return s[2].contains("ROLE_BOSS") || s[2].contains("ROLE_USER");
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private HttpHeaders createHeaders(String jwt) {
        return new HttpHeaders() {{
            set("Authorization", jwt);
        }};
    }
}
