package com.example.todoapp.service;

import com.example.todoapp.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    // Admin methods (unchanged)
    List<Task> getAllTasks();
    Optional<Task> getTaskById(String id);

    // New user-specific methods
    List<Task> getUserTasks(String userId);
    Optional<Task> getUserTaskById(String id, String userId);

    // Updated methods to include user ID
    Task createTask(Task task, String userId);
    Task updateTask(String id, Task task);
    Task updateTask(String id, Task task, String userId);

    // User-specific task filtering
    List<Task> getUserTasksByStatus(String status, String userId);
    List<Task> searchUserTasksByTitle(String title, String userId);
    Page<Task> getPaginatedUserTasks(String userId, Pageable pageable);
    List<Task> searchUserTasksFullText(String searchTerm, String userId);
    Page<Task> getPaginatedUserTasksByStatus(String status, String userId, Pageable pageable);
    void softDeleteTask(String id);
    void softDeleteTask(String id, String userId);

    // Admin-only methods (unchanged)
    void restoreTask(String id);
    void permanentDeleteTask(String id);

    // These methods will need to be maintained for backward compatibility and admin use
    List<Task> getTasksByStatus(String status);
    List<Task> searchTasksByTitle(String title);
    Page<Task> getPaginatedTasks(Pageable pageable);
    List<Task> searchTasksFullText(String searchTerm);
    Page<Task> getPaginatedTasksByStatus(String status, Pageable pageable);
}