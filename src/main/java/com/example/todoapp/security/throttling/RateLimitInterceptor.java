package com.example.todoapp.security.throttling;

import com.example.todoapp.config.rate_limit.RateLimitConfig;
import com.example.todoapp.exception.RateLimitExceededException;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private Map<String, Bucket> buckets;

    @Autowired
    private RateLimitConfig rateLimitConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userId = getUserId();

        // Skip rate limiting for authentication endpoints
        if (request.getRequestURI().startsWith("/api/auth")) {
            return true;
        }

        Bucket bucket = getBucketForUser(userId);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return true;
        } else {
            response.addHeader("X-Rate-Limit-Retry-After-Seconds",
                    String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000));
            throw new RateLimitExceededException("Rate limit exceeded. Try again in " +
                    probe.getNanosToWaitForRefill() / 1_000_000_000 + " seconds");
        }
    }

    private Bucket getBucketForUser(String userId) {
        return buckets.computeIfAbsent(userId, key -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String role = auth != null && !auth.getAuthorities().isEmpty() ?
                    auth.getAuthorities().iterator().next().getAuthority() : "ROLE_USER";
            return rateLimitConfig.createNewBucket(role);
        });
    }

    private String getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "anonymous";
    }
}