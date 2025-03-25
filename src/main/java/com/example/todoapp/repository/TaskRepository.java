package com.example.todoapp.repository;

import com.example.todoapp.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {
    // Existing methods
    List<Task> findByStatus(String status);
    List<Task> findByTitleContainingIgnoreCase(String title);

    // New methods for Phase 3
    // Paginated task retrieval
    Page<Task> findAll(Pageable pageable);

    // Full-text search across title and description
    @Query(value = "{ $or: [ " +
            "{ 'title': { $regex: ?0, $options: 'i' } }, " +
            "{ 'description': { $regex: ?0, $options: 'i' } } " +
            "] }")
    List<Task> searchTasks(String searchTerm);

    // Find tasks by status with pagination
    Page<Task> findByStatus(String status, Pageable pageable);

    // Find non-deleted tasks
    List<Task> findByDeletedFalse();

    // Optional specific task by ID excluding deleted
    Optional<Task> findByIdAndDeletedFalse(String id);
}