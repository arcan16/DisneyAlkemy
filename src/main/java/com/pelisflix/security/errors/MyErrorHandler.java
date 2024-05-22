package com.pelisflix.security.errors;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class MyErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> manejadorDeError400(MethodArgumentNotValidException e){
        var errores = e.getFieldErrors().stream().map(individualDataError::new).toList();
        return ResponseEntity.badRequest().body(errores);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> manejadorDeErroresInvalidTypeData(MethodArgumentTypeMismatchException e){
        return ResponseEntity.badRequest().body("{\"err\":\"Tipo de dato incorrecto\"}");
    }

    private record individualDataError(String campo, String error){
        public individualDataError(FieldError error){
            this(error.getField(),error.getDefaultMessage());
        }
    }
}
