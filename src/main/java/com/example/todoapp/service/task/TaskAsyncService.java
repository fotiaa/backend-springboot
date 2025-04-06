package com.example.todoapp.service.task;

import com.example.todoapp.model.Task;

import java.util.concurrent.CompletableFuture;

public interface TaskAsyncService {

    /**
     * Process task creation asynchronously
     *
     * @param task The task to be processed
     * @param userId The user ID
     * @return CompletableFuture containing the created task
     */
    CompletableFuture<Task> processTaskCreationAsync(Task task, String userId);

    /**
     * Process task update asynchronously
     *
     * @param id Task ID
     * @param task Task with updated fields
     * @param userId User ID
     * @return CompletableFuture containing the updated task
     */
    CompletableFuture<Task> processTaskUpdateAsync(String id, Task task, String userId);

    /**
     * Process soft deletion asynchronously
     *
     * @param id Task ID
     * @param userId User ID
     * @return CompletableFuture with void result
     */
    CompletableFuture<Void> processSoftDeleteAsync(String id, String userId);

    /**
     * Process task indexing for search asynchronously
     *
     * @param task The task to be indexed
     * @return CompletableFuture with void result
     */
    CompletableFuture<Void> indexTaskForSearchAsync(Task task);
}