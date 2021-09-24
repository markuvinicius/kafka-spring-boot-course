package com.learn.kafka.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.kafka.domain.Book;
import com.learn.kafka.domain.LibraryEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics={"library-events"}, partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
public class LibraryEventsControllerIntegrationTest {

    private final String postEndPoint = new String("/v1/libraryevent");

    private ObjectMapper objectMapper;
    private TestRestTemplate testRestTemplate;
    private Consumer<Integer,String> evertConsumer;

    @Autowired
    public LibraryEventsControllerIntegrationTest(TestRestTemplate testRestTemplate,
                                                  ObjectMapper objectMapper){
        this.testRestTemplate = testRestTemplate;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void setUp() {
        // Setup components
    }

    @Test
    public void postLibraryEvent(){

        Book book = Book.builder()
                            .bookAuthor("Marku Vinicius")
                            .bookName("Kafka Spring Boot")
                            .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                                        .libraryEventId(null)
                                        .book(book)
                                        .build();
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<LibraryEvent> requestBody = new HttpEntity<>(libraryEvent,headers);

        ResponseEntity<LibraryEvent> responseEntity = testRestTemplate.exchange(postEndPoint,
                HttpMethod.POST,
                requestBody,
                LibraryEvent.class);

        assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode());


    }

}
