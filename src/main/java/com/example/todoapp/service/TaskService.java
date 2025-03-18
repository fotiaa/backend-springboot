package com.example.todoapp.service;

import com.example.todoapp.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    List<Task> getAllTasks();
    Optional<Task> getTaskById(String id);
    Task createTask(Task task);
    Task updateTask(String id, Task task);
    void deleteTask(String id);
    List<Task> getTasksByStatus(String status);
    List<Task> searchTasksByTitle(String title);
}