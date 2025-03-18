package com.example.todoapp.repository;

import com.example.todoapp.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {
    // Find tasks by status
    List<Task> findByStatus(String status);

    // Find tasks by title containing the given string (case-insensitive)
    List<Task> findByTitleContainingIgnoreCase(String title);
}