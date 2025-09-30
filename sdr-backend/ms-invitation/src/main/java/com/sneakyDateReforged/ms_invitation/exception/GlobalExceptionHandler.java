package com.sneakyDateReforged.ms_invitation.exception;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static String requestId() {
        return MDC.get("requestId");
    }

    private static ResponseEntity<ApiError> build(HttpStatus status, String message, HttpServletRequest req) {
        ApiError body = new ApiError(
                status.value(),
                status.getReasonPhrase(),
                message,
                req.getRequestURI(),
                requestId()
        );
        return ResponseEntity.status(status).body(body);
    }

    /* ---------- Exceptions MÉTIER (custom) ---------- */

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    /* ---------- Exceptions STANDARD fréquentes ---------- */

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), req);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(IllegalStateException ex, HttpServletRequest req) {
        // souvent utilisé pour des conflits métier -> 409
        return build(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "Contrainte de données violée", req);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Payload JSON invalide", req);
    }

    /* ---------- Validation (Bean Validation / Spring Validation) ---------- */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError body = new ApiError(
                status.value(), status.getReasonPhrase(), "Validation failed", req.getRequestURI(), requestId()
        );
        List<ApiError.FieldViolation> v = new ArrayList<>();
        for (var err : ex.getBindingResult().getAllErrors()) {
            if (err instanceof FieldError fe) {
                v.add(new ApiError.FieldViolation(fe.getField(), fe.getDefaultMessage()));
            } else {
                v.add(new ApiError.FieldViolation(err.getObjectName(), err.getDefaultMessage()));
            }
        }
        body.violations = v;
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError body = new ApiError(
                status.value(), status.getReasonPhrase(), "Validation failed", req.getRequestURI(), requestId()
        );
        List<ApiError.FieldViolation> v = new ArrayList<>();
        ex.getConstraintViolations().forEach(cv -> {
            String field = cv.getPropertyPath() != null ? cv.getPropertyPath().toString() : "<unknown>";
            v.add(new ApiError.FieldViolation(field, cv.getMessage()));
        });
        body.violations = v;
        return ResponseEntity.status(status).body(body);
    }

    /* ---------- Feign (ms-rdv / futurs ms-notif) ---------- */

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ApiError> handleFeignNotFound(FeignException.NotFound ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, "Ressource distante introuvable", req);
    }

    @ExceptionHandler(FeignException.Forbidden.class)
    public ResponseEntity<ApiError> handleFeignForbidden(FeignException.Forbidden ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, "Accès refusé par le service distant", req);
    }

    @ExceptionHandler(FeignException.Unauthorized.class)
    public ResponseEntity<ApiError> handleFeignUnauthorized(FeignException.Unauthorized ex, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, "Authentification requise vers le service distant", req);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiError> handleFeignGeneric(FeignException ex, HttpServletRequest req) {
        // On choisit 502 (Bad Gateway) pour signaler un échec d'appel en aval
        return build(HttpStatus.BAD_GATEWAY, "Service distant indisponible", req);
    }

    /* ---------- Fallback ---------- */

    @ExceptionHandler(org.springframework.web.ErrorResponseException.class)
    public ResponseEntity<ApiError> handleErrorResponse(org.springframework.web.ErrorResponseException ex,
                                                        jakarta.servlet.http.HttpServletRequest req) {
        // HttpStatusCode -> HttpStatus (si possible), sinon 500
        int code = ex.getStatusCode().value();
        org.springframework.http.HttpStatus status =
                org.springframework.http.HttpStatus.resolve(code) != null
                        ? org.springframework.http.HttpStatus.resolve(code)
                        : org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

        // Message : d’abord le ProblemDetail.detail, sinon le message de l’exception
        String message = (ex.getBody() != null && ex.getBody().getDetail() != null)
                ? ex.getBody().getDetail()
                : ex.getMessage();

        return build(status, message, req);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAny(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne", req);
    }
}
