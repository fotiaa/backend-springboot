package com.example.todoapp.controller.task;

import com.example.todoapp.model.Task;
import com.example.todoapp.notification.dto.NotificationRequest;
import com.example.todoapp.notification.service.NotificationService;
import com.example.todoapp.service.task.AdminTaskService;
import com.example.todoapp.service.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTaskController {

    @Autowired
    private AdminTaskService adminTaskService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/all")
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(adminTaskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable String id) {
        return adminTaskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable String id, @RequestBody Task task) {
        try {
            Task updatedTask = adminTaskService.updateTask(id, task);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable String status) {
        return ResponseEntity.ok(adminTaskService.getTasksByStatus(status));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchTasks(@RequestParam String title) {
        return ResponseEntity.ok(adminTaskService.searchTasksByTitle(title));
    }

    @GetMapping("/paginated")
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
        return ResponseEntity.ok(adminTaskService.getPaginatedTasks(pageRequest));
    }

    @GetMapping("/status/{status}/paginated")
    public ResponseEntity<Page<Task>> getPaginatedTasksByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok(adminTaskService.getPaginatedTasksByStatus(status, pageRequest));
    }

    @GetMapping("/search/advanced")
    public ResponseEntity<List<Task>> advancedSearchTasks(@RequestParam String query) {
        return ResponseEntity.ok(adminTaskService.searchTasksFullText(query));
    }

    @PostMapping("/{id}/soft-delete")
    public ResponseEntity<Void> softDeleteTask(@PathVariable String id) {
        adminTaskService.softDeleteTask(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restoreTask(@PathVariable String id) {
        adminTaskService.restoreTask(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> permanentDeleteTask(@PathVariable String id) {
        adminTaskService.permanentDeleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) {
        notificationService.processNotification(request);
        return ResponseEntity.ok("Notification sent successfully");
    }
}