package com.example.todoapp.controller.task;

import com.example.todoapp.model.Task;
import com.example.todoapp.service.task.core.TaskService;
import com.example.todoapp.utils.TaskUtil;
import com.example.todoapp.websocket.WebSocketMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskUtil taskUtil;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/tasks")
    @SendToUser("/queue/notifications")
    public WebSocketMessage processTaskMessage(WebSocketMessage message, Principal principal) {
        // Process incoming WebSocket messages from clients
        // This method handles any client-side events that need to be processed
        // For now, just echo the message back to the client
        return message;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Task>> getUserTasks() {
        String userId = taskUtil.getCurrentUserId();
        return ResponseEntity.ok(taskService.getUserTasks(userId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Task> getTaskById(@PathVariable String id) {
        String userId = taskUtil.getCurrentUserId();
        return taskService.getUserTaskById(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        String userId = taskUtil.getCurrentUserId();
        Task createdTask = taskService.createTask(task, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Task> updateTask(@PathVariable String id, @RequestBody Task task) {
        String userId = taskUtil.getCurrentUserId();
        try {
            Task updatedTask = taskService.updateTask(id, task, userId);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable String status) {
        String userId = taskUtil.getCurrentUserId();
        return ResponseEntity.ok(taskService.getUserTasksByStatus(status, userId));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Task>> searchTasks(@RequestParam String title) {
        String userId = taskUtil.getCurrentUserId();
        return ResponseEntity.ok(taskService.searchUserTasksByTitle(title, userId));
    }

    @GetMapping("/paginated")
    @PreAuthorize("hasRole('USER')")
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
        return ResponseEntity.ok(taskService.getPaginatedUserTasks(userId, pageRequest));
    }

    @GetMapping("/status/{status}/paginated")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<Task>> getPaginatedTasksByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String userId = taskUtil.getCurrentUserId();
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok(taskService.getPaginatedUserTasksByStatus(status, userId, pageRequest));
    }

    @GetMapping("/search/advanced")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Task>> advancedSearchTasks(@RequestParam String query) {
        String userId = taskUtil.getCurrentUserId();
        return ResponseEntity.ok(taskService.searchUserTasksFullText(query, userId));
    }

    @PostMapping("/{id}/soft-delete")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> softDeleteTask(@PathVariable String id) {
        String userId = taskUtil.getCurrentUserId();
        taskService.softDeleteTask(id, userId);
        return ResponseEntity.ok().build();
    }
}