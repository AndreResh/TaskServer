package com.example.taskserver.controller;


import com.example.taskserver.domain.Task;
import com.example.taskserver.exeption.ApiRequestExceptions;
import com.example.taskserver.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }
    @PostMapping
    public ResponseEntity<Task> saveTask(@RequestBody Task task){
        Task task2=service.findByName(task);
        if(task2!=null){
            throw new ApiRequestExceptions("The task is in DB");
        }
        return ResponseEntity.ok(service.save(task));
    }
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(){
        return ResponseEntity.ok(service.findAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Task> findTaskById(@PathVariable("id") Long id){
        Task task=service.findById(id);
        return Objects.isNull(task)?ResponseEntity.notFound().build():ResponseEntity.ok(task);
    }
    @DeleteMapping("/{id}")
    public void  deleteTask(@PathVariable("id") Long id){
        service.delete(id);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable("id") Long id,@RequestBody Task task){
        return ResponseEntity.ok(service.update(id,task));
    }
}
