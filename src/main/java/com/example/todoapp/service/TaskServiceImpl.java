package com.example.todoapp.service;

import com.example.todoapp.exception.TaskNotFoundException;
import com.example.todoapp.model.Task;
import com.example.todoapp.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findByDeletedFalse();
    }

    @Override
    public Task createTask(Task task) {
        task.setCreatedAt(LocalDateTime.now());
        task.setStatus(task.getStatus() != null ? task.getStatus() : Task.TaskStatus.TODO);
        return taskRepository.save(task);
    }

    @Override
    public List<Task> getTasksByStatus(String status) {
        return taskRepository.findByStatus(status).stream()
                .filter(task -> !task.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> searchTasksByTitle(String title) {
        return taskRepository.findByTitleContainingIgnoreCase(title).stream()
                .filter(task -> !task.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public Page<Task> getPaginatedTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    @Override
    public List<Task> searchTasksFullText(String searchTerm) {
        return taskRepository.searchTasks(searchTerm).stream()
                .filter(task -> !task.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public Page<Task> getPaginatedTasksByStatus(String status, Pageable pageable) {
        return taskRepository.findByStatus(status, pageable);
    }

    @Override
    public Optional<Task> getTaskById(String id) {
        return taskRepository.findByIdAndDeletedFalse(id);
    }


    @Override
    public Task updateTask(String id, Task task) {
        return taskRepository.findByIdAndDeletedFalse(id)
                .map(existingTask -> {
                    existingTask.setTitle(task.getTitle());
                    existingTask.setDescription(task.getDescription());
                    existingTask.setStatus(task.getStatus());
                    existingTask.setUpdatedAt(LocalDateTime.now());
                    return taskRepository.save(existingTask);
                })
                .orElseThrow(() -> new TaskNotFoundException("Task not found or deleted with id: " + id));
    }

    @Override
    public void deleteTask(String id) {
        Task task = taskRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
        taskRepository.delete(task);
    }

    @Override
    public void softDeleteTask(String id) {
        Task task = taskRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
        task.setDeleted(true);
        taskRepository.save(task);
    }

    @Override
    public void restoreTask(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
        task.setDeleted(false);
        taskRepository.save(task);
    }

    @Override
    public void permanentDeleteTask(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
        if (task.isDeleted()) {
            taskRepository.delete(task);
        } else {
            throw new IllegalStateException("Task must be soft-deleted first");
        }
    }
}