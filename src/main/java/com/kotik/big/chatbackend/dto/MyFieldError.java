package com.kotik.big.chatbackend.dto;

import lombok.Data;
import org.springframework.validation.FieldError;

@Data
public class MyFieldError {
    private String field;
    private String code;
    private String message;

    public MyFieldError(FieldError error) {
        this.field = error.getField();
        this.code = error.getCode();
        this.message = error.getDefaultMessage();
    }
}
