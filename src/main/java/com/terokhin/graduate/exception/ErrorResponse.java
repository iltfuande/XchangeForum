package com.terokhin.graduate.exception;

import static java.util.stream.Collectors.toSet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

@Data
public class ErrorResponse {
    @JsonIgnore
    private HttpStatus status;

    private String message;

    private Set<FieldConstraintViolation> constraintViolations;

    @JsonProperty("status")
    public int getRawStatus() {
        return status.value();
    }

    public ErrorResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public ErrorResponse(HttpStatus status, String message, Set<ConstraintViolation<?>> constraintViolations) {
        this.status = status;
        this.message = message;
        this.constraintViolations = constraintViolations.stream()
                .map(FieldConstraintViolation::from)
                .collect(toSet());
    }

    public ErrorResponse(HttpStatus status, String message, List<ObjectError> errors) {
        this.status = status;
        this.message = message;
        this.constraintViolations = errors.stream()
                .filter(FieldError.class::isInstance)
                .map (FieldError.class::cast)
                .map(FieldConstraintViolation::from)
                .collect(toSet());

    }

    private record FieldConstraintViolation(String field, Object invalidValue, String message) {
        public static FieldConstraintViolation from(ConstraintViolation<?> constraintViolation) {
            return new FieldConstraintViolation(
                    constraintViolation.getRootBeanClass().getSimpleName() + "." + constraintViolation.getPropertyPath(),
                    constraintViolation.getInvalidValue(),
                    constraintViolation.getMessage()
            );
        }
        public static FieldConstraintViolation from(FieldError error) {
            return new FieldConstraintViolation(
                    error.getObjectName() + "." + error.getField(),
                    error.getRejectedValue(),
                    error.getDefaultMessage()
            );
        }
    }
}