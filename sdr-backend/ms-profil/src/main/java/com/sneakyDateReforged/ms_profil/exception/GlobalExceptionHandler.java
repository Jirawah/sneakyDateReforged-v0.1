package com.sneakyDateReforged.ms_profil.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String,Object>> notFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("timestamp", LocalDateTime.now(), "status", 404, "error", "Not Found", "message", ex.getMessage())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> validation(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(
                Map.of("timestamp", LocalDateTime.now(), "status", 400, "error", "Validation",
                        "message", ex.getBindingResult().getAllErrors().get(0).getDefaultMessage())
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,Object>> badRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(
                Map.of("timestamp", LocalDateTime.now(), "status", 400, "error", "Bad Request", "message", ex.getMessage())
        );
    }
}
