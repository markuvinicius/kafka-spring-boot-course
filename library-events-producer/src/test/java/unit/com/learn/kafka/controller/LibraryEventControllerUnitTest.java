package com.learn.kafka.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.kafka.domain.Book;
import com.learn.kafka.domain.LibraryEvent;
import com.learn.kafka.domain.LibraryEventType;
import com.learn.kafka.producer.LibraryEventProducer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(LibraryEventsController.class)
public class LibraryEventControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    LibraryEventProducer libraryEventProducer;

    @Test
    void postLibraryEvent() throws Exception {
        //GIVEN
        Book book = Book.builder()
                .bookId(123)
                .bookAuthor("Marku Vinicius")
                .bookName("Kafka Spring Boot")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventType(LibraryEventType.NEW)
                .libraryEventId(null)
                .book(book)
                .build();

        String payload = objectMapper.writeValueAsString(libraryEvent);

        Mockito
                .when(libraryEventProducer.sendLibraryEventWithHeaders(Mockito.isA(LibraryEvent.class)))
                    .thenReturn(null);

        //WHEN
        mockMvc.perform( post("/v1/libraryevent")
                            .content(payload)
                            .contentType(MediaType.APPLICATION_JSON) )
                .andExpect(status().isCreated());
    }

    @Test
    void postLibraryEvent_4xx() throws Exception {
        //GIVEN
        Book book = Book.builder()
                .bookId(123)
                .bookAuthor(null)
                .bookName(null)
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventType(LibraryEventType.NEW)
                .libraryEventId(null)
                .book(book)
                .build();

        String payload = objectMapper.writeValueAsString(libraryEvent);

        Mockito
                .when(libraryEventProducer.sendLibraryEventWithHeaders(Mockito.isA(LibraryEvent.class)))
                .thenReturn(null);

        //WHEN
        String expectedErrorMessage = "book.bookAuthor - must not be blank, book.bookName - must not be blank";
        mockMvc.perform( post("/v1/libraryevent")
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON) )
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(expectedErrorMessage));
    }

    @Test
    void putLibraryEvent() throws Exception {
        //GIVEN
        Book book = Book.builder()
                .bookId(123)
                .bookAuthor("Marku Vinicius")
                .bookName("Kafka Spring Boot")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventType(LibraryEventType.NEW)
                .libraryEventId(123)
                .book(book)
                .build();

        String payload = objectMapper.writeValueAsString(libraryEvent);

        Mockito
                .when(libraryEventProducer.sendLibraryEventWithHeaders(Mockito.isA(LibraryEvent.class)))
                .thenReturn(null);

        //WHEN
        mockMvc.perform( put("/v1/libraryevent")
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON) )
                .andExpect(status().isOk());
    }

    @Test
    void putLibraryEvent_4xx() throws Exception {
        //GIVEN
        Book book = Book.builder()
                .bookId(123)
                .bookAuthor("a")
                .bookName("b")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventType(LibraryEventType.NEW)
                .libraryEventId(null)
                .book(book)
                .build();

        String payload = objectMapper.writeValueAsString(libraryEvent);

        Mockito
                .when(libraryEventProducer.sendLibraryEventWithHeaders(Mockito.isA(LibraryEvent.class)))
                .thenReturn(null);

        //WHEN
        String expectedErrorMessage = "Please inform the Library Event ID";
        mockMvc.perform( put("/v1/libraryevent")
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON) )
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(expectedErrorMessage));
    }
}
