package com.kotik.big.chatbackend.controller;

import com.kotik.big.chatbackend.dto.MyFieldError;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

public abstract class ErrorHandlingAbstractController {
    protected ResponseEntity<?> getErrors(BindingResult bindingResult) {
        return ResponseEntity.badRequest().body(bindingResult.getFieldErrors().stream().map(MyFieldError::new));
    }
}
