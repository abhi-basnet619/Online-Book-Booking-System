package com.bookstore.exception;

import com.bookstore.dto.common.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex, req);
    }

    @ExceptionHandler({BadRequestException.class, MethodArgumentNotValidException.class, BindException.class, ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex, req);
    }

    @ExceptionHandler({UnauthorizedException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleUnauthorized(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, ex, req);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(AccessDeniedException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, ex, req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex, req);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, Exception ex, HttpServletRequest req) {
        String path = req.getRequestURI();
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(safeMessage(ex))
                .path(path)
                .build();
        return ResponseEntity.status(status).body(body);
    }

    private String safeMessage(Exception ex) {
        if (ex instanceof MethodArgumentNotValidException manve) {
            if (manve.getBindingResult().getFieldError() != null) {
                return manve.getBindingResult().getFieldError().getField() + ": " + manve.getBindingResult().getFieldError().getDefaultMessage();
            }
        }
        if (ex instanceof BindException be) {
            if (be.getBindingResult().getFieldError() != null) {
                return be.getBindingResult().getFieldError().getField() + ": " + be.getBindingResult().getFieldError().getDefaultMessage();
            }
        }
        return ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
    }
}
