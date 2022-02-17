package com.example.taskserver.service;


import com.example.taskserver.domain.Task;
import com.example.taskserver.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TaskService {
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public Task save(Task task) {
        List<Task> list = findAll();
        Long number;
        try {
            number = list.stream().max((o1, o2) -> {
                return (int) (o1.getId() - o2.getId());
            }).get().getId();
        } catch (NoSuchElementException e) {
            number = 0L;
        }
        task.setId(++number);
        return repository.save(task);
    }

    public Task findById(Long id) {
        return repository.getTaskById(id);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<Task> findAll() {
        return repository.findAllTasks();
    }

    public Task findByName(String name) {
        return repository.findByName(name);
    }

    public Task update(Long id, Task t) {
        Task task1 = findById(id);
        task1.setId(id);
        if (t.getName() != null) {
            task1.setName(t.getName());
        }
        if (t.getDescription() != null) {
            task1.setDescription(t.getDescription());
        }
        if (t.getStrength()!= null) {
            task1.setStrength(t.getStrength());
        }
        if (t.getNumberOfPeople() != null) {
            task1.setNumberOfPeople(t.getNumberOfPeople());
        }
        repository.update(id,task1.getName(),task1.getDescription(), task1.getStrength(), task1.getNumberOfPeople());
        return task1;
    }
}
