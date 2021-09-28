package com.learn.kafka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.kafka.domain.Book;
import com.learn.kafka.domain.LibraryEvent;
import com.learn.kafka.domain.LibraryEventType;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics={"library-events"}, partitions = 3)
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"
})
public class LibraryEventsControllerIntegrationTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<Integer,String> consumer;
    private final String postEndPoint = new String("/v1/libraryevent");

    @BeforeEach
    void setUp() {
        Map<String, Object> configs = new HashMap(KafkaTestUtils.consumerProps("group-1","true", embeddedKafkaBroker));
        consumer = new DefaultKafkaConsumerFactory(configs, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    @Timeout(5)
    public void postLibraryEvent() throws JsonProcessingException, InterruptedException {

        //GIVEN
        Book book = Book.builder()
                            .bookId(123)
                            .bookAuthor("Marku Vinicius")
                            .bookName("Kafka Spring Boot")
                            .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                                        .libraryEventType(LibraryEventType.NEW)
                                        //.libraryEventId(null)
                                        .book(book)
                                        .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<LibraryEvent> requestBody = new HttpEntity<>(libraryEvent,headers);

        //WHEN
        ResponseEntity<LibraryEvent> responseEntity = testRestTemplate.exchange(postEndPoint,
                HttpMethod.POST,
                requestBody,
                LibraryEvent.class);

        //THEN
        assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode());
        ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, "library-events");
        LibraryEvent value = objectMapper.readValue( consumerRecord.value(), LibraryEvent.class );

        assertEquals( value , libraryEvent );
    }

    @Test
    @Timeout(5)
    public void putLibraryEvent() throws JsonProcessingException, InterruptedException {

        //GIVEN
        Book book = Book.builder()
                .bookId(123)
                .bookAuthor("Marku Vinicius")
                .bookName("Kafka Spring Boot")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventType(LibraryEventType.UPDATE)
                .libraryEventId(1234)
                .book(book)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<LibraryEvent> requestBody = new HttpEntity<>(libraryEvent,headers);

        //WHEN
        ResponseEntity<LibraryEvent> responseEntity = testRestTemplate.exchange(postEndPoint,
                HttpMethod.PUT, requestBody, LibraryEvent.class);

        //THEN
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, "library-events");
        LibraryEvent value = objectMapper.readValue( consumerRecord.value(), LibraryEvent.class );

        assertEquals( value , libraryEvent );
    }

}
