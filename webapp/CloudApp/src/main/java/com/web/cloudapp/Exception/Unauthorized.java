package com.web.cloudapp.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.UNAUTHORIZED)
public class Unauthorized extends RuntimeException {

    String fieldName;
    String operation;

    public Unauthorized(String fieldName, String opertion) {
        super(String.format("Unauthorized to %s %s", opertion, fieldName));
        this.fieldName = fieldName;
        this.operation = opertion;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getOperation() {
        return operation;
    }
}
