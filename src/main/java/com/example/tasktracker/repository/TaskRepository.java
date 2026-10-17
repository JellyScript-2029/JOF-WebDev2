package com.example.tasktracker.repository;

import com.example.tasktracker.model.Task;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TaskRepository {

    private final List<Task> tasks = new ArrayList<>();
    private long nextId = 1;

    public List<Task> findAll() {
        return new ArrayList<>(tasks);
    }

    public Optional<Task> findById(Long id) {
        return tasks.stream()
                .filter(task -> task.getId().equals(id))
                .findFirst();
    }

    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(nextId++);
        }
        tasks.add(task);
        return task;
    }
}
