package com.rituraj.ecommerce.middleware;

import com.rituraj.ecommerce.util.JwtUtil;
import com.rituraj.ecommerce.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

    @Aspect
    @Component
    @Order(0)
    public class JwtAspect {
    private JwtUtil jwtUtil;

        public JwtAspect(JwtUtil jwtUtil) {
            this.jwtUtil = jwtUtil;
        }

        private static final ThreadLocal<String> currentUserId = new ThreadLocal<>();

    @Before("@annotation(AuthRequired)")
    public void validateToken(JoinPoint joinPoint) {
        // Assuming the token is resolved from the HTTP request
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = jwtUtil.resolveToken(request);

        if (token != null && jwtUtil.validateToken(token)) {
            // Extract the user ID from the token
            String userId = jwtUtil.extractUserId(token); // Implement this method to extract user ID
            currentUserId.set(userId);
        } else {
            throw new UnauthorizedException("Invalid JWT Token");
        }
    }

    public static String getCurrentUserId() {
        return currentUserId.get();
    }

    @After("@annotation(AuthRequired)")
    public void clear() {
        currentUserId.remove();
    }
    }