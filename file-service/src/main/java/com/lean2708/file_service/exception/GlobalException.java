package com.lean2708.auth_service.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j(topic = "GLOBAL-EXCEPTION")
@RestControllerAdvice
public class GlobalException {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllException(Exception ex, WebRequest request) {

        // location
        StackTraceElement element = ex.getStackTrace()[0];
        String location = String.format("%s.%s()", element.getClassName(), element.getMethodName());

        log.error("Tại: {} - Đã xảy ra lỗi: {}", location, ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }


    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDataException(InvalidDataException e, WebRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }


    @ExceptionHandler({ConstraintViolationException.class,
            MissingServletRequestParameterException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleValidationException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        List<String> messages = new ArrayList<>();

        // MethodArgumentNotValidException
        if (ex instanceof MethodArgumentNotValidException) {
            BindingResult bindingResult = ((MethodArgumentNotValidException) ex).getBindingResult();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                messages.add(fieldError.getDefaultMessage());
            }
            errorResponse.setError("Invalid Payload");
        }
        // ConstraintViolationException
        else if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException violationException = (ConstraintViolationException) ex;
            for (ConstraintViolation<?> violation : violationException.getConstraintViolations()) {
                messages.add(violation.getMessage());
            }
            errorResponse.setError("Invalid Parameter");
        }
        // MissingServletRequestParameterException
        else if (ex instanceof MissingServletRequestParameterException) {
            MissingServletRequestParameterException missingParamEx = (MissingServletRequestParameterException) ex;
            errorResponse.setError("Invalid Parameter");
            messages.add("Thiếu tham số: " + missingParamEx.getParameterName());
        }

        String errorMessages = String.join(". ", messages);
        errorResponse.setMessage(errorMessages);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }


    @ExceptionHandler({ForBiddenException.class, AccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(Exception e, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(errorResponse);
    }


    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthenticatedException(UnauthenticatedException e, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse);
    }



}
