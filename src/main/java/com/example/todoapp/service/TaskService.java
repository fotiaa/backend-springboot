package com.example.todoapp.service;

import com.example.todoapp.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    // Existing methods
    List<Task> getAllTasks();
    Optional<Task> getTaskById(String id);
    Task createTask(Task task);
    Task updateTask(String id, Task task);
    void deleteTask(String id);
    List<Task> getTasksByStatus(String status);
    List<Task> searchTasksByTitle(String title);

    // New methods for Phase 3
    Page<Task> getPaginatedTasks(Pageable pageable);
    List<Task> searchTasksFullText(String searchTerm);
    Page<Task> getPaginatedTasksByStatus(String status, Pageable pageable);

    // Add new method for soft delete
    void softDeleteTask(String id);
    void restoreTask(String id);
    void permanentDeleteTask(String id);
}