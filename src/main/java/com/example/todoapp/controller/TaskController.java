package com.example.todoapp.controller;

import com.example.todoapp.model.Task;
import com.example.todoapp.service.TaskService;
import com.example.todoapp.utils.TaskUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    
    @Autowired
    private TaskUtil taskUtil;

    // Admin endpoint to get all tasks
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    // Regular users get only their tasks
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Task>> getUserTasks() {
        String userId = taskUtil.getCurrentUserId();
        // Admin, return all tasks, otherwise return only user's tasks
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.ok(taskService.getAllTasks());
        }
        return ResponseEntity.ok(taskService.getUserTasks(userId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Task> getTaskById(@PathVariable String id) {
        String userId = taskUtil.getCurrentUserId();
        // Admin, can access any task
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return taskService.getTaskById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        // Regular users can only access their own tasks
        return taskService.getUserTaskById(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        String userId = taskUtil.getCurrentUserId();
        Task createdTask = taskService.createTask(task, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Task> updateTask(@PathVariable String id, @RequestBody Task task) {
        String userId = taskUtil.getCurrentUserId();
        try {
            // Admin, can update any task
            if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                    .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                Task updatedTask = taskService.updateTask(id, task);
                return ResponseEntity.ok(updatedTask);
            }
            // Regular users can only update their own tasks
            Task updatedTask = taskService.updateTask(id, task, userId);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable String status) {
        String userId = taskUtil.getCurrentUserId();
        // Admin, can see all tasks of a status
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.ok(taskService.getTasksByStatus(status));
        }
        // Regular users can only see their own tasks of a status
        return ResponseEntity.ok(taskService.getUserTasksByStatus(status, userId));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Task>> searchTasks(@RequestParam String title) {
        String userId = taskUtil.getCurrentUserId();
        // Admin, can search all tasks
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.ok(taskService.searchTasksByTitle(title));
        }
        // Regular users can only search their own tasks
        return ResponseEntity.ok(taskService.searchUserTasksByTitle(title, userId));
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
        String userId = taskUtil.getCurrentUserId();
        Sort sort = Sort.by(
                sortDirection.equalsIgnoreCase("asc") ?
                        Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy
        );
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        // Admin, can see all paginated tasks
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.ok(taskService.getPaginatedTasks(pageRequest));
        }
        // Regular users can only see their own paginated tasks
        return ResponseEntity.ok(taskService.getPaginatedUserTasks(userId, pageRequest));
    }

    // Paginated tasks by status
    @GetMapping("/status/{status}/paginated")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<Task>> getPaginatedTasksByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String userId = taskUtil.getCurrentUserId();
        PageRequest pageRequest = PageRequest.of(page, size);

        // Admin, can see all paginated tasks of a status
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.ok(taskService.getPaginatedTasksByStatus(status, pageRequest));
        }
        // Regular users can only see their own paginated tasks of a status
        return ResponseEntity.ok(taskService.getPaginatedUserTasksByStatus(status, userId, pageRequest));
    }

    // Enhanced search endpoint
    @GetMapping("/search/advanced")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Task>> advancedSearchTasks(@RequestParam String query) {
        String userId = taskUtil.getCurrentUserId();
        // Admin, can perform advanced search on all tasks
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.ok(taskService.searchTasksFullText(query));
        }
        // Regular users can only perform advanced search on their own tasks
        return ResponseEntity.ok(taskService.searchUserTasksFullText(query, userId));
    }

    @PostMapping("/{id}/soft-delete")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> softDeleteTask(@PathVariable String id) {
        String userId = taskUtil.getCurrentUserId();
        // Admin, can soft delete any task
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            taskService.softDeleteTask(id);
        } else {
            // Regular users can only soft delete their own tasks
            taskService.softDeleteTask(id, userId);
        }
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