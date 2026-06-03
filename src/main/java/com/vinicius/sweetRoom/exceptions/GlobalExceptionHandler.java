package com.vinicius.sweetRoom.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(DuplicatedResourceException.class)
    public ResponseEntity<String> handleDuplicatedResourceException(DuplicatedResourceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ReservationValidationException.class)
    public ResponseEntity<String> handleReservationValidation(ReservationValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleInvalidEnum(HttpMessageNotReadableException ex) {
        // We log the real error to the console for debugging
        System.out.println("JSON Parse Error: " + ex.getMessage());

        // But we return a safe, generic message to the user
        String safeErrorMessage = "Malformed JSON request. Please check your data types and enum values.";

        // Optional: Make it slightly smarter if it's an enum error
        if (ex.getMessage() != null && ex.getMessage().contains("not one of the values accepted for Enum class")) {
            safeErrorMessage = "Invalid value provided for one of the fields. Please check accepted values.";
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(safeErrorMessage);
    }
}
