package com.ajeet.hospital.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    // =========================================================
    // BAD CREDENTIALS
    // =========================================================

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> handleBadCredentials(
            BadCredentialsException exception) {

        return Map.of(
                "status", 401,
                "message", "Invalid username or password"
        );
    }


    // =========================================================
    // DEPARTMENT NOT FOUND
    // =========================================================

    @ExceptionHandler(DepartmentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleDepartmentNotFound(
            DepartmentNotFoundException exception) {

        return Map.of(
                "status", 404,
                "message", exception.getMessage()
        );
    }


    // =========================================================
    // VALIDATION
    // =========================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationErrors(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors =
                new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        return Map.of(
                "status", 400,
                "message", "Validation failed",
                "errors", errors
        );
    }


    // =========================================================
    // DOCTOR NOT FOUND
    // =========================================================

    @ExceptionHandler(DoctorNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleDoctorNotFound(
            DoctorNotFoundException exception) {

        return Map.of(
                "status", 404,
                "message", exception.getMessage()
        );
    }


    // =========================================================
    // PATIENT NOT FOUND
    // =========================================================

    @ExceptionHandler(PatientNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handlePatientNotFound(
            PatientNotFoundException exception) {

        return Map.of(
                "status", 404,
                "message", exception.getMessage()
        );
    }


    // =========================================================
    // APPOINTMENT CONFLICT
    // =========================================================

    @ExceptionHandler(AppointmentConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleAppointmentConflict(
            AppointmentConflictException exception) {

        return Map.of(
                "status", 409,
                "message", exception.getMessage()
        );
    }


    // =========================================================
    // APPOINTMENT NOT FOUND
    // =========================================================

    @ExceptionHandler(AppointmentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleAppointmentNotFound(
            AppointmentNotFoundException exception) {

        return Map.of(
                "status", 404,
                "message", exception.getMessage()
        );
    }


    // =========================================================
    // MEDICAL RECORD NOT FOUND
    // =========================================================

    @ExceptionHandler(MedicalRecordNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleMedicalRecordNotFound(
            MedicalRecordNotFoundException exception) {

        return Map.of(
                "status", 404,
                "message", exception.getMessage()
        );
    }


    // =========================================================
    // BILL NOT FOUND
    // =========================================================

    @ExceptionHandler(BillNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleBillNotFound(
            BillNotFoundException exception) {

        return Map.of(
                "status", 404,
                "message", exception.getMessage()
        );
    }


    // =========================================================
    // BILL ALREADY EXISTS
    // =========================================================

    @ExceptionHandler(BillAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleBillAlreadyExists(
            BillAlreadyExistsException exception) {

        return Map.of(
                "status", 409,
                "message", exception.getMessage()
        );
    }


    // =========================================================
    // ILLEGAL ARGUMENT
    // =========================================================

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleIllegalArgument(
            IllegalArgumentException exception) {

        return Map.of(
                "status", 400,
                "message", exception.getMessage()
        );
    }


    // =========================================================
    // ILLEGAL STATE
    // =========================================================

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleIllegalState(
            IllegalStateException exception) {

        return Map.of(
                "status", 400,
                "message", exception.getMessage()
        );
    }


    // =========================================================
    // REFRESH TOKEN
    // =========================================================

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> handleRefreshTokenNotFound(
            RefreshTokenNotFoundException exception) {

        return Map.of(
                "status", 401,
                "message", exception.getMessage()
        );
    }

}