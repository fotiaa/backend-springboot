package com.example.todoapp.service.task;

import com.example.todoapp.model.Task;
import com.example.todoapp.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public List<Task> getUserTasks(String userId) {
        return taskRepository.findByCreatedByAndDeletedFalse(userId);
    }

    @Override
    public Optional<Task> getUserTaskById(String id, String userId) {
        return taskRepository.findByIdAndCreatedByAndDeletedFalse(id, userId);
    }

    @Override
    public Task createTask(Task task, String userId) {
        task.setCreatedBy(userId);
        task.setCreatedAt(LocalDateTime.now());
        task.setDeleted(false);
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(String id, Task task, String userId) {
        return getUserTaskById(id, userId)
                .map(existingTask -> {
                    existingTask.setTitle(task.getTitle());
                    existingTask.setDescription(task.getDescription());
                    existingTask.setStatus(task.getStatus());
                    existingTask.setUpdatedAt(LocalDateTime.now());
                    return taskRepository.save(existingTask);
                })
                .orElseThrow(() -> new RuntimeException("Task not found or not authorized"));
    }

    @Override
    public List<Task> getUserTasksByStatus(String status, String userId) {
        return taskRepository.findByCreatedByAndStatusAndDeletedFalse(userId, status);
    }

    @Override
    public List<Task> searchUserTasksByTitle(String title, String userId) {
        // This method assumes that title search is part of full text search
        return searchUserTasksFullText(title, userId);
    }

    @Override
    public Page<Task> getPaginatedUserTasks(String userId, Pageable pageable) {
        return taskRepository.findByCreatedBy(userId, pageable);
    }

    @Override
    public List<Task> searchUserTasksFullText(String searchTerm, String userId) {
        return taskRepository.searchTasksByUser(userId, searchTerm);
    }

    @Override
    public Page<Task> getPaginatedUserTasksByStatus(String status, String userId, Pageable pageable) {
        return taskRepository.findByCreatedByAndStatus(userId, status, pageable);
    }

    @Override
    public void softDeleteTask(String id, String userId) {
        getUserTaskById(id, userId)
                .map(task -> {
                    task.setDeleted(true);
                    return taskRepository.save(task);
                })
                .orElseThrow(() -> new RuntimeException("Task not found or not authorized"));
    }
}