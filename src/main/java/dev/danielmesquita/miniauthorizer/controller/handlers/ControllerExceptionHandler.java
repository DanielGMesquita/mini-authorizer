package dev.danielmesquita.miniauthorizer.controller.handlers;

import dev.danielmesquita.miniauthorizer.dto.CustomError;
import dev.danielmesquita.miniauthorizer.exception.CardAlreadyExistsException;
import dev.danielmesquita.miniauthorizer.exception.ResourceNotFoundException;
import dev.danielmesquita.miniauthorizer.exception.TransactionException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<CustomError> resourceNotFound(
      ResourceNotFoundException e, HttpServletRequest request) {
    HttpStatus status = HttpStatus.NOT_FOUND;
    CustomError error =
        new CustomError(Instant.now(), 404, e.getMessage(), request.getRequestURI());
    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(CardAlreadyExistsException.class)
  public ResponseEntity<CustomError> cardAlreadyExists(
      CardAlreadyExistsException e, HttpServletRequest request) {
    HttpStatus status = HttpStatus.UNPROCESSABLE_CONTENT;
    CustomError error =
        new CustomError(Instant.now(), 422, e.getMessage(), request.getRequestURI());
    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(TransactionException.class)
  public ResponseEntity<CustomError> databaseException(
      TransactionException e, HttpServletRequest request) {
    HttpStatus status = HttpStatus.UNPROCESSABLE_CONTENT;
    CustomError error =
        new CustomError(Instant.now(), 422, e.getMessage(), request.getRequestURI());
    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<CustomError> handleValidationException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    String errorMessage =
        ex.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .findFirst()
            .orElse("Validation error");
    CustomError error = new CustomError(Instant.now(), 400, errorMessage, request.getRequestURI());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }
}
