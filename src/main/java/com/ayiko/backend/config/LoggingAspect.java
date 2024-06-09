package com.ayiko.backend.config;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.ayiko.backend.controller.*Controller.*(..))")
    public void controller() {}

    @Before("controller()")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof HttpServletRequest) {
                HttpServletRequest request = (HttpServletRequest) arg;
                String line;
                StringBuilder payload = new StringBuilder();
                try {
                    BufferedReader reader = request.getReader();
                    while ((line = reader.readLine()) != null) {
                        payload.append(line).append('\n');
                    }
                    logger.info("Request payload: {}", payload.toString());
                } catch (IOException e) {
                    logger.error("Error reading request payload", e);
                }
            }
        }
    }
}

