package com.example.todoapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "tasks")
public class Task {
    @Id
    private String id;

    @TextIndexed
    private String title;

    @TextIndexed
    private String description;

    private TaskStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted = false;

    private String createdBy;

    public enum TaskStatus {
        TODO, IN_PROGRESS, COMPLETED
    }
}