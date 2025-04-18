package com.example.todoapp.config;

import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import com.mongodb.client.MongoClients;

@Configuration
@EnableMongoRepositories(basePackages = "com.example.todoapp.repository")
public class MongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String connectionString;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory() {
        MongoClient mongoClient = MongoClients.create(connectionString);
        // Register shutdown hook to clean up resources when the app stops
        Runtime.getRuntime().addShutdownHook(new Thread(() -> mongoClient.close()));
        return new SimpleMongoClientDatabaseFactory(mongoClient, databaseName);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoDatabaseFactory());
    }
}