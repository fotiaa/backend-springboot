package com.example.todoapp.service.task;

import com.example.todoapp.model.Task;
import com.example.todoapp.repository.TaskRepository;
import com.example.todoapp.utils.CacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class TaskAsyncServiceImpl implements TaskAsyncService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CacheUtil cacheUtil;

    @Async("taskExecutor")
    @Override
    public CompletableFuture<Task> processTaskCreationAsync(Task task, String userId) {
        task.setUpdatedAt(LocalDateTime.now());
        Task savedTask = taskRepository.save(task);

        // Invalidate relevant caches
        cacheUtil.invalidateCreationCaches(userId, String.valueOf(task.getStatus()));

        // Cache the newly created task
        cacheUtil.cacheTask(savedTask.getId(), savedTask);

        return CompletableFuture.completedFuture(savedTask);
    }

    @Async("taskExecutor")
    @Override
    public CompletableFuture<Task> processTaskUpdateAsync(String id, Task task, String userId) {
        Optional<Task> existingTaskOpt = taskRepository.findByIdAndCreatedByAndDeletedFalse(id, userId);

        if (existingTaskOpt.isPresent()) {
            Task existingTask = existingTaskOpt.get();
            String oldStatus = String.valueOf(existingTask.getStatus());

            existingTask.setTitle(task.getTitle());
            existingTask.setDescription(task.getDescription());
            existingTask.setStatus(task.getStatus());
            existingTask.setUpdatedAt(LocalDateTime.now());

            Task updatedTask = taskRepository.save(existingTask);

            // Invalidate all relevant caches
            cacheUtil.invalidateTaskCaches(id, userId, oldStatus, String.valueOf(task.getStatus()));

            // Update the cache with the new task
            cacheUtil.cacheTask(id, updatedTask);
            cacheUtil.cacheUserTask(id, userId, updatedTask);

            return CompletableFuture.completedFuture(updatedTask);
        } else {
            return CompletableFuture.failedFuture(new RuntimeException("Task not found"));
        }
    }

    @Async("taskExecutor")
    @Override
    public CompletableFuture<Void> processSoftDeleteAsync(String id, String userId) {
        Optional<Task> taskOpt = taskRepository.findByIdAndCreatedByAndDeletedFalse(id, userId);

        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            String status = String.valueOf(task.getStatus());
            task.setDeleted(true);
            task.setUpdatedAt(LocalDateTime.now());
            taskRepository.save(task);

            // Invalidate all relevant caches
            cacheUtil.invalidateDeletionCaches(id, userId, status);
        }

        return CompletableFuture.completedFuture(null);
    }

    @Async("taskExecutor")
    @Override
    public CompletableFuture<Void> indexTaskForSearchAsync(Task task) {
        // Here you would implement logic to index the task in a search engine
        // This is just a placeholder as the actual implementation depends on your search technology

        // Cache the indexed task
        cacheUtil.cacheTask(task.getId(), task);

        return CompletableFuture.completedFuture(null);
    }
}