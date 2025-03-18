package com.example.todoapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "tasks")
public class Task {
    @Id
    private String id;

    private String title;
    private String description;
    private String status;
    private LocalDateTime createdAt;

    // Constructor without ID for creating new tasks
    public Task(String title, String description, String status) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }
}