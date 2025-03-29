package com.example.todoapp.service.task;

import com.example.todoapp.model.Task;
import com.example.todoapp.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminTaskServiceImpl implements AdminTaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findByDeletedFalse();
    }

    @Override
    public Optional<Task> getTaskById(String id) {
        return taskRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    public Task updateTask(String id, Task task) {
        return getTaskById(id)
                .map(existingTask -> {
                    existingTask.setTitle(task.getTitle());
                    existingTask.setDescription(task.getDescription());
                    existingTask.setStatus(task.getStatus());
                    existingTask.setDueDate(task.getDueDate());
                    existingTask.setPriority(task.getPriority());
                    return taskRepository.save(existingTask);
                })
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @Override
    public void softDeleteTask(String id) {
        getTaskById(id)
                .map(task -> {
                    task.setDeleted(true);
                    return taskRepository.save(task);
                })
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @Override
    public void restoreTask(String id) {
        Optional<Task> task = taskRepository.findById(id);
        task.ifPresent(t -> {
            t.setDeleted(false);
            taskRepository.save(t);
        });
    }

    @Override
    public void permanentDeleteTask(String id) {
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

    @Override
    public Page<Task> getPaginatedTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    @Override
    public List<Task> searchTasksFullText(String searchTerm) {
        return taskRepository.searchTasks(searchTerm);
    }

    @Override
    public Page<Task> getPaginatedTasksByStatus(String status, Pageable pageable) {
        return taskRepository.findByStatus(status, pageable);
    }
}