package com.learn.kafka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learn.kafka.domain.LibraryEvent;
import com.learn.kafka.domain.LibraryEventType;
import com.learn.kafka.producer.LibraryEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@Slf4j
public class LibraryEventsController {

    @Autowired
    private LibraryEventProducer libraryEventProducer;

    @PostMapping("/v1/libraryevent")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException, MissingServletRequestParameterException {
        libraryEvent.setLibraryEventType( LibraryEventType.NEW );
        libraryEventProducer.sendLibraryEventWithHeaders(libraryEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PutMapping("/v1/libraryevent")
    public ResponseEntity<?> putLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException, MissingServletRequestParameterException {
        if (libraryEvent.getLibraryEventId()==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please inform the Library Event ID");
        }

        libraryEvent.setLibraryEventType( LibraryEventType.UPDATE );
        libraryEventProducer.sendLibraryEventWithHeaders(libraryEvent);
        return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
    }
}
