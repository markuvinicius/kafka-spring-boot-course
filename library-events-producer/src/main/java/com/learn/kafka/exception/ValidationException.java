package com.learn.kafka.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class ValidationException extends ApiOperationException{
    private String object;
    private String field;
    private Object rejectedValue;
    private String message;

    ValidationException(String object, String message) {
        this.object = object;
        this.message = message;
    }
}
