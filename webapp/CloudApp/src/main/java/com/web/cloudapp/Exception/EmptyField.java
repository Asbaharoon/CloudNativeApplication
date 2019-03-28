package com.web.cloudapp.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EmptyField extends RuntimeException {
    private String fieldName;

    public EmptyField(String fieldName) {
        super(String.format("%s cannot be empty ", fieldName));
        this.fieldName = fieldName;
    }


    public String getFieldName() {
        return fieldName;
    }
}
