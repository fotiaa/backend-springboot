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

    public JwtAuthResponse(String token, String username, Set<String> roles) {
        this.token = token;
        this.username = username;
        this.roles = roles;
    }
}