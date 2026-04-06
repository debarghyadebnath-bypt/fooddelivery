package com.example.foodelivery.advices;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import com.example.foodelivery.exceptions.ResourceNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex){
        ApiError apiError = new ApiError();
        apiError.setHttpStatus(HttpStatus.BAD_REQUEST);
        apiError.setMessage("Validation failed");
        apiError.setSubErrors(ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList());
        return ResponseEntity.badRequest().body(apiError);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(DataIntegrityViolationException ex){
        ApiError apiError = new ApiError();
        apiError.setHttpStatus(HttpStatus.CONFLICT);
        apiError.setMessage("Data integrity violation");
        apiError.setSubErrors(List.of(ex.getMostSpecificCause().getMessage()));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex){
        ApiError apiError = new ApiError();
        apiError.setHttpStatus(HttpStatus.NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        apiError.setSubErrors(List.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalStateException(IllegalStateException ex){
        ApiError apiError = new ApiError();
        apiError.setHttpStatus(HttpStatus.BAD_REQUEST);
        apiError.setMessage(ex.getMessage());
        apiError.setSubErrors(List.of());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }
}
