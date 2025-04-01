package com.example.todoapp.service.task;

import com.example.todoapp.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AdminTaskService {
    List<Task> getAllTasks();
    Optional<Task> getTaskById(String id);
    Task updateTask(String id, Task task);
    void softDeleteTask(String id);
    void restoreTask(String id);
    void permanentDeleteTask(String id);
    List<Task> getTasksByStatus(String status);
    List<Task> searchTasksByTitle(String title);
    Page<Task> getPaginatedTasks(Pageable pageable);
    List<Task> searchTasksFullText(String searchTerm);
    Page<Task> getPaginatedTasksByStatus(String status, Pageable pageable);
    void clearAdminCaches();
}