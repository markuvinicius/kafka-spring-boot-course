package com.learn.kafka.validator.domainValidator;

public interface ControllerValidatorHandler<T> {
    RuntimeException handleValidator(T validator);
}
