package com.sneakyDateReforged.ms_invitation.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    public Instant timestamp = Instant.now();
    public int status;
    public String error;
    public String message;
    public String path;
    public String requestId;            // si présent dans MDC
    public List<FieldViolation> violations;

    public ApiError() {}

    public ApiError(int status, String error, String message, String path, String requestId) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.requestId = requestId;
    }

    public static class FieldViolation {
        public String field;
        public String message;

        public FieldViolation() {}
        public FieldViolation(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }
}
