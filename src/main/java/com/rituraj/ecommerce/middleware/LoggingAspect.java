package com.rituraj.ecommerce.middleware;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Order(1)
public class LoggingAspect {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // Controller layer logging
    @Around("execution(* com.rituraj.ecommerce.controller.*.*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodExecution(joinPoint, "Controller");
    }

    // Service layer logging
    @Around("execution(* com.rituraj.ecommerce.service.*.*(..))")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodExecution(joinPoint, "Service");
    }

    // Repository layer logging
    @Around("execution(* com.rituraj.ecommerce.repository.*.*(..))")
    public Object logRepository(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodExecution(joinPoint, "Repository");
    }

    // Security logging - updated to cover all security-related components
    @Around("securityPointcut()")
    public Object logSecurity(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodExecution(joinPoint, "Security");
    }

    // Define pointcut for security-related components
    @Pointcut("execution(* com.rituraj.ecommerce.config.SecurityConfig.*(..)) || " +
            "execution(* com.rituraj.ecommerce.util.JwtUtil.*(..)) || " +
            "execution(* com.rituraj.ecommerce.util.JwtAuthenticationFilter.*(..)) || " +
            "execution(* com.rituraj.ecommerce.util.JwtAspect.*(..))")
    public void securityPointcut() {}

    private Object logMethodExecution(ProceedingJoinPoint joinPoint, String layer) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();

        log.info("[{}] {}.{} - Started - Arguments: {}",
                layer, className, methodName, Arrays.toString(arguments));

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();

            log.info("[{}] {}.{} - Completed - Duration: {}ms - Result: {}",
                    layer, className, methodName, (endTime - startTime), result);

            return result;
        } catch (Exception e) {
            log.error("[{}] {}.{} - Failed - Error: {}",
                    layer, className, methodName, e.getMessage(), e);
            throw e;
        }
    }
}