package com.learn.kafka.validator.domainValidator;

import br.com.fluentvalidator.AbstractValidator;
import br.com.fluentvalidator.predicate.ObjectPredicate;
import com.learn.kafka.domain.LibraryEvent;
import com.learn.kafka.validator.domainValidator.BookValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class LibraryEventValidator extends AbstractValidator<LibraryEvent> {

    @Autowired
    BookValidator bookValidator;

    @Override
    public void rules() {
        setPropertyOnContext("libraryEvent");

        ruleFor( libraryEvent -> libraryEvent.getBook())
                .whenever( Predicate.not( ObjectPredicate.nullValue() ))
                    .withValidator( bookValidator);
    }
}
