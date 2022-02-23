package com.example.taskserver.service;

import com.example.taskserver.Configuration.TaskClientProperties;
import com.example.taskserver.domain.Band;
import com.example.taskserver.domain.Task;
import com.example.taskserver.domain.User;
import com.example.taskserver.domain.Weapon;
import com.example.taskserver.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class TaskService {
    //        private final static Logger log= LoggerFactory.getLogger(TaskController.class);
    private final TaskRepository repository;
    private final RestTemplate restTemplate;
    private final TaskClientProperties properties;


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
        if(t.getBandId()!=null){
            task1.setBandId(t.getBandId());
        }
        log.info("Task which updating: {}", task1);
        repository.update(id, task1.getName(), task1.getDescription(), task1.getStrength(), task1.getNumberOfPeople());
        return task1;
    }

    public void addTaskToBand(Long id, Band band) {
        log.info("add to Task with id: {}. Band name: {}", id, band.getBandName());
        band = restTemplate.exchange(properties.getUrlBands() + band.getBandName(),
                HttpMethod.GET, null, new ParameterizedTypeReference<Band>() {
                }).getBody();
        if (band != null) {
            Long bandId = band.getId();
            repository.addTaskToBand(id, bandId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

    }

    public void makeTaskCompleted(Long id) {
        Map<String, List<Weapon>> mapOfWeapons =
                restTemplate.exchange(properties.getUrlWeapons(),
                        HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, List<Weapon>>>() {
                        }).getBody();
        List<User> mapOfUsers =
                restTemplate.exchange(properties.getUrlUsers(),
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<User>>() {
                        }).getBody();
        Weapon weapon = mapOfWeapons.get("weapons").stream().filter(o -> o.getTask_id().equals(id)).findFirst().get();
        User user = mapOfUsers.stream().filter(o -> o.getTaskId().equals(id)).findFirst().get();
        Long weaponId = weapon.getId();
        Long userId = user.getUserId();
        Map<String, Object> mapForWeapons = new HashMap<>();
        mapForWeapons.put("task_id", 0L);
        Map<String, Object> mapForUsers = new HashMap<>();
        mapForUsers.put("taskId", 0L);
        restTemplate.patchForObject(properties.getUrlWeapons() + weaponId, new HttpEntity<>(mapForWeapons), Object.class);
        restTemplate.patchForObject(properties.getUrlUsers() + userId, new HttpEntity<>(mapForUsers), Object.class);
        repository.makeTaskCompleted(id);
    }
}
