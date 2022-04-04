package com.example.taskserver.controller;


import com.example.taskserver.dto.Band;
import com.example.taskserver.domain.Task;
import com.example.taskserver.exeption.ApiRequestExceptions;
import com.example.taskserver.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Task> saveTask(@Valid @RequestBody Task task, Errors errors, HttpServletRequest request) {
        if (!service.isTokenValidBoss(request)) {
            log.error("Not valid JWT Token");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        log.info("Task for saving: {}", task);
        if (errors.hasErrors()) {
            log.error("Not valid task: {}", task);
            throw new ApiRequestExceptions("Task is not valid");
        }
        Task task2 = service.findByName(task.getName());
        if (task2 != null) {
            log.error("Task in DB: {}", task);
            throw new ApiRequestExceptions("The task is in DB");
        }
        return ResponseEntity.ok(service.save(task));
    }

    @GetMapping
    public ResponseEntity<?> getTask(@RequestParam(value = "taskName", required = false) String name, HttpServletRequest request) {
        if (!service.isTokenValidBoss(request)) {
            log.error("Not valid JWT Token");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        if (name == null) {
            log.info("Getting all tasks");
            return ResponseEntity.ok(service.findAllTasks());
        } else {
            log.info("Searching task with name: {}", name);
            return ResponseEntity.ok(service.findByName(name));
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> findTaskById(@PathVariable("id") Long id, HttpServletRequest request) {
        if (!service.isTokenValidBossAndUser(request)) {
            log.error("Not valid JWT Token");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        log.info("Searching task with id: {}", id);
        Task task = service.findById(id);
        if (Objects.isNull(task)) {
            log.info("Can't found task with id: {}", id);
            throw new ApiRequestExceptions("Not found");
        } else {
            return ResponseEntity.ok(task);
        }
    }


    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable("id") Long id ,HttpServletRequest request) {
        if (!service.isTokenValidBoss(request)) {
            log.error("Not valid JWT Token");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        log.info("Deleting task with id: {}", id);
        service.delete(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable("id") Long id, @RequestBody Task task, HttpServletRequest request) {
        if (!service.isTokenValidBoss(request)) {
            log.error("Not valid JWT Token");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        log.info("Task with id: {}. And body: {}", id, task);
        return ResponseEntity.ok(service.update(id, task));
    }

    @PatchMapping("/{id}/addBand")
    public ResponseEntity<Task> addToBand(@PathVariable("id") Long id, @RequestBody Band bandName, HttpServletRequest request) {
        if (!service.isTokenValidBoss(request)) {
            log.error("Not valid JWT Token");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        log.info("Add task with name {}", bandName);
        service.addTaskToBand(id, bandName, request);
        return ResponseEntity.ok(service.findById(id));
    }

    @PatchMapping("/{id}/completed")
    public ResponseEntity<Task> makeCompleted(@PathVariable("id") Long id, HttpServletRequest request) {
        if (!service.isTokenValidBoss(request)) {
            log.error("Not valid JWT Token");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        log.info("Make task with id: {} completed", id);
        service.makeTaskCompleted(id,request);
        return ResponseEntity.ok(service.findById(id));
    }

}
