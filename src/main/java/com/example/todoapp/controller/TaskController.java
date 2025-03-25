package com.example.todoapp.controller;

import com.example.todoapp.model.Task;
import com.example.todoapp.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Task> getTaskById(@PathVariable String id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Task> createTask(@RequestBody Task task, Principal principal) {
        // You can add creator information using Principal if needed
        // task.setCreatedBy(principal.getName());
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Task> updateTask(@PathVariable String id, @RequestBody Task task) {
        try {
            Task updatedTask = taskService.updateTask(id, task);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")  // Only ADMIN can delete tasks
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable String status) {
        return ResponseEntity.ok(taskService.getTasksByStatus(status));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Task>> searchTasks(@RequestParam String title) {
        return ResponseEntity.ok(taskService.searchTasksByTitle(title));
    }


    // New pagination endpoint
    @GetMapping("/paginated")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<Task>> getPaginatedTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Sort sort = Sort.by(
                sortDirection.equalsIgnoreCase("asc") ?
                        Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy
        );
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(taskService.getPaginatedTasks(pageRequest));
    }

    // Paginated tasks by status
    @GetMapping("/status/{status}/paginated")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<Task>> getPaginatedTasksByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok(taskService.getPaginatedTasksByStatus(status, pageRequest));
    }

    // Enhanced search endpoint
    @GetMapping("/search/advanced")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Task>> advancedSearchTasks(@RequestParam String query) {
        return ResponseEntity.ok(taskService.searchTasksFullText(query));
    }

    @PostMapping("/{id}/soft-delete")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> softDeleteTask(@PathVariable String id) {
        taskService.softDeleteTask(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> restoreTask(@PathVariable String id) {
        taskService.restoreTask(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/permanent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> permanentDeleteTask(@PathVariable String id) {
        taskService.permanentDeleteTask(id);
        return ResponseEntity.noContent().build();
    }

}