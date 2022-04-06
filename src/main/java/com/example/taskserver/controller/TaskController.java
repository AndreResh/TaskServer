package com.example.taskserver.controller;


import com.example.taskserver.dto.Band;
import com.example.taskserver.domain.Task;
import com.example.taskserver.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Task> saveTask(@Valid @RequestBody Task task, HttpServletRequest request) {
        service.isTokenValidBoss(request);
        log.info("Task for saving: {}", task);
        return ResponseEntity.ok(service.save(task));
    }

    @GetMapping
    public ResponseEntity<?> getTask(@RequestParam(value = "taskName", required = false) String name, HttpServletRequest request) {
        service.isTokenValidBoss(request);
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
        service.isTokenValidBossAndUser(request);
        log.info("Searching task with id: {}", id);
        return ResponseEntity.ok(service.findById(id));
    }


    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable("id") Long id, HttpServletRequest request) {
        service.isTokenValidBoss(request);
        log.info("Deleting task with id: {}", id);
        service.delete(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable("id") Long id, @RequestBody Task task, HttpServletRequest request) {
        service.isTokenValidBoss(request);
        log.info("Task with id: {}. And body: {}", id, task);
        return ResponseEntity.ok(service.update(id, task));
    }

    @PatchMapping("/{id}/addBand")
    public ResponseEntity<Task> addToBand(@PathVariable("id") Long id, @RequestBody Band bandName, HttpServletRequest request) {
        service.isTokenValidBoss(request);
        log.info("Add task with name {}", bandName);
        service.addTaskToBand(id, bandName, request);
        return ResponseEntity.ok(service.findById(id));
    }

    @PatchMapping("/{id}/completed")
    public ResponseEntity<Task> makeCompleted(@PathVariable("id") Long id, HttpServletRequest request) {
        service.isTokenValidBoss(request);
        log.info("Make task with id: {} completed", id);
        service.makeTaskCompleted(id, request);
        return ResponseEntity.ok(service.findById(id));
    }
}
