package com.rituraj.ecommerce.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e){
        log.error("Global Exception Handler - Error: {}", e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND,
                e.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {InvalidInputException.class})
    public ResponseEntity<ErrorResponse> handleInvalidInputException(InvalidInputException e){
        log.error("Global Exception Handler - Error: {}", e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                e.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {UnauthorizedException.class})
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException e){
        log.error("Global Exception Handler - Error: {}", e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED,
                e.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {EntityAlreadyExistsException.class})
    public ResponseEntity<ErrorResponse> handleEntityAlreadyExistsException(EntityAlreadyExistsException e){
        log.error("Global Exception Handler - Error: {}", e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT,
                e.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {EntityCreationException.class})
    public ResponseEntity<ErrorResponse> handleEntityCreationException(EntityCreationException e){
        log.error("Global Exception Handler - Error: {}", e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                e.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {EntityDeletionException.class})
    public ResponseEntity<ErrorResponse> handleEntityDeletionException(EntityDeletionException e){
        log.error("Global Exception Handler - Error: {}", e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND,
                e.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {EntityPushException.class})
    public ResponseEntity<ErrorResponse> handleEntityPushException(EntityPushException e){
        log.error("Global Exception Handler - Error: {}", e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                e.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {EntityUpdationException.class})
    public ResponseEntity<ErrorResponse> handleEntityUpdationException(EntityUpdationException e){
        log.error("Global Exception Handler - Error: {}", e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                e.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }



}
