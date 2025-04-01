package com.example.todoapp.constant;

import java.util.concurrent.TimeUnit;

public class CacheConstant {
    // TTL values
    public static final long SHORT_TTL = 15;
    public static final long MEDIUM_TTL = 30;
    public static final long LONG_TTL = 60;
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;

    // Cache key prefixes
    public static final String TASK_PREFIX = "task::";
    public static final String USER_TASKS_PREFIX = "userTasks::";
    public static final String USER_TASKS_BY_STATUS_PREFIX = "userTasksByStatus::";
    public static final String PAGINATED_USER_TASKS_PREFIX = "paginatedUserTasks::";
    public static final String PAGINATED_USER_TASKS_BY_STATUS_PREFIX = "paginatedUserTasksByStatus::";
    public static final String ALL_TASKS = "allTasks";
    public static final String TASK_BY_ID_PREFIX = "taskById::";
    public static final String TASKS_BY_STATUS_PREFIX = "tasksByStatus::";
    public static final String PAGINATED_TASKS_PREFIX = "paginatedTasks::";
    public static final String PAGINATED_TASKS_BY_STATUS_PREFIX = "paginatedTasksByStatus::";
    public static final String INDEXED_TASK_PREFIX = "indexedTask::";

    // Cache names for clearing
    public static final String USER_TASKS_CACHE = "userTasks";
    public static final String TASK_CACHE = "task";
    public static final String USER_TASKS_BY_STATUS_CACHE = "userTasksByStatus";
    public static final String PAGINATED_USER_TASKS_CACHE = "paginatedUserTasks";
    public static final String PAGINATED_USER_TASKS_BY_STATUS_CACHE = "paginatedUserTasksByStatus";
    public static final String TASK_BY_ID_CACHE = "taskById";
    public static final String TASKS_BY_STATUS_CACHE = "tasksByStatus";
    public static final String PAGINATED_TASKS_CACHE = "paginatedTasks";
    public static final String PAGINATED_TASKS_BY_STATUS_CACHE = "paginatedTasksByStatus";

    private CacheConstant() {
        // Private constructor to prevent instantiation
    }
}