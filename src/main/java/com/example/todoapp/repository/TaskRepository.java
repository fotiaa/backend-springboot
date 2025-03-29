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
    List<Task> findByStatus(String status);
    List<Task> findByTitleContainingIgnoreCase(String title);

    // New methods to find tasks by creator
    List<Task> findByCreatedByAndDeletedFalse(String userId);
    Page<Task> findByCreatedBy(String userId, Pageable pageable);
    Page<Task> findByCreatedByAndStatus(String userId, String status, Pageable pageable);
    List<Task> findByCreatedByAndStatusAndDeletedFalse(String userId, String status);

    @Query(value = "{ $and: [ " +
            "{ 'createdBy': ?0 }, " +
            "{ $or: [ " +
            "{ 'title': { $regex: ?1, $options: 'i' } }, " +
            "{ 'description': { $regex: ?1, $options: 'i' } } " +
            "] } ] }")
    List<Task> searchTasksByUser(String userId, String searchTerm);

    // Existing methods
    Page<Task> findAll(Pageable pageable);

    @Query(value = "{ $or: [ " +
            "{ 'title': { $regex: ?0, $options: 'i' } }, " +
            "{ 'description': { $regex: ?0, $options: 'i' } } " +
            "] }")
    List<Task> searchTasks(String searchTerm);

    Page<Task> findByStatus(String status, Pageable pageable);
    List<Task> findByDeletedFalse();
    Optional<Task> findByIdAndDeletedFalse(String id);

    // New method to find task by ID and user ID (for authorization)
    Optional<Task> findByIdAndCreatedByAndDeletedFalse(String id, String userId);

}