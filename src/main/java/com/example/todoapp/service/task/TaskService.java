package com.example.todoapp.service.task;

import com.example.todoapp.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    // User-specific methods
    List<Task> getUserTasks(String userId);
    Optional<Task> getUserTaskById(String id, String userId);
    Task createTask(Task task, String userId);
    Task updateTask(String id, Task task, String userId);

    // User-specific task filtering
    List<Task> getUserTasksByStatus(String status, String userId);
    List<Task> searchUserTasksByTitle(String title, String userId);
    Page<Task> getPaginatedUserTasks(String userId, Pageable pageable);
    List<Task> searchUserTasksFullText(String searchTerm, String userId);
    Page<Task> getPaginatedUserTasksByStatus(String status, String userId, Pageable pageable);
    void softDeleteTask(String id, String userId);
}