package com.example.taskserver.controller;


import com.example.taskserver.dto.Band;
import com.example.taskserver.domain.Task;
import com.example.taskserver.dto.BandDTO;
import com.example.taskserver.dto.TaskDTO;
import com.example.taskserver.dto.TaskForUpdateDTO;
import com.example.taskserver.service.TaskService;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService service;
    private final Map<String,String> map =Map.of("bandName", "");

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", example = "Bearer access_token")
    public ResponseEntity<Task> saveTask(@Valid @RequestBody TaskDTO task, HttpServletRequest request) {
        service.isTokenValidBoss(request);
        log.info("Task for saving: {}", task);
        return ResponseEntity.ok(service.save(task));
    }

    @GetMapping
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", example = "Bearer access_token")
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
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", example = "Bearer access_token")
    public ResponseEntity<Task> findTaskById(@PathVariable("id") Long id, HttpServletRequest request) {
        service.isTokenValidBossAndUser(request);
        log.info("Searching task with id: {}", id);
        return ResponseEntity.ok(service.findById(id));
    }


    @DeleteMapping("/{id}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", example = "Bearer access_token")
    public ResponseEntity<?> deleteTask(@PathVariable("id") Long id, HttpServletRequest request) {
        service.isTokenValidBoss(request);
        log.info("Deleting task with id: {}", id);
        service.delete(id);
        return  ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", example = "Bearer access_token")
    public ResponseEntity<Task> updateTask(@PathVariable("id") Long id,@Valid @RequestBody TaskForUpdateDTO task, HttpServletRequest request) {
        service.isTokenValidBoss(request);
        log.info("Task with id: {}. And body: {}", id, task);
        return ResponseEntity.ok(service.update(id, task));
    }

    @PatchMapping("/{id}/addBand")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", example = "Bearer access_token")
    public ResponseEntity<Task> addToBand(@PathVariable("id") Long id, @RequestBody BandDTO bandDTO, HttpServletRequest request) {
        service.isTokenValidBoss(request);
        log.info("Add task with name {}", bandDTO);
        service.addTaskToBand(id, bandDTO.getBandName(), request);
        return ResponseEntity.ok(service.findById(id));
    }

    @PatchMapping("/{id}/completed")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", example = "Bearer access_token")
    public ResponseEntity<Task> makeCompleted(@PathVariable("id") Long id, HttpServletRequest request) {
        service.isTokenValidBoss(request);
        log.info("Make task with id: {} completed", id);
        service.makeTaskCompleted(id, request);
        return ResponseEntity.ok(service.findById(id));
    }
}
