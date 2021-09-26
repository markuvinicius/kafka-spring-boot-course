package com.learn.kafka.validator.domainValidator;

import br.com.fluentvalidator.AbstractValidator;
import br.com.fluentvalidator.predicate.StringPredicate;
import com.learn.kafka.domain.Book;
import org.springframework.stereotype.Component;

import static java.util.function.Predicate.not;

@Component
public class BookValidator extends AbstractValidator<Book> {
    @Override
    public void rules() {
        setPropertyOnContext("book");

        ruleFor(Book::getBookName)
                .must(not(StringPredicate.stringEmptyOrNull()))
                .withMessage("Book name must not be empty")
                .withFieldName("Name");

        ruleFor(Book::getBookAuthor)
                .must(not(StringPredicate.stringEmptyOrNull()))
                .withMessage("Book Author name must not be empty")
                .withFieldName("author");

    }
}
