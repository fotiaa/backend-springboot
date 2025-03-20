// JwtAuthResponse.java
package com.example.todoapp.payload;

import lombok.Data;

import java.util.Set;

@Data
public class JwtAuthResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private Set<String> roles;
}