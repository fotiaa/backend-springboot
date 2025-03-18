package com.example.todoapp.service;

import com.example.todoapp.model.Task;
import com.example.todoapp.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Optional<Task> getTaskById(String id) {
        return taskRepository.findById(id);
    }

    @Override
    public Task createTask(Task task) {
        task.setCreatedAt(LocalDateTime.now());
        if (task.getStatus() == null) {
            task.setStatus("TODO");
        }
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(String id, Task taskDetails) {
        return taskRepository.findById(id)
                .map(existingTask -> {
                    existingTask.setTitle(taskDetails.getTitle());
                    existingTask.setDescription(taskDetails.getDescription());
                    existingTask.setStatus(taskDetails.getStatus());
                    // Don't update createdAt field
                    return taskRepository.save(existingTask);
                })
                .orElseThrow(() -> new RuntimeException("Task not found with id " + id));
    }

    @Override
    public void deleteTask(String id) {
        taskRepository.deleteById(id);
    }

    @Override
    public List<Task> getTasksByStatus(String status) {
        return taskRepository.findByStatus(status);
    }

    @Override
    public List<Task> searchTasksByTitle(String title) {
        return taskRepository.findByTitleContainingIgnoreCase(title);
    }
}