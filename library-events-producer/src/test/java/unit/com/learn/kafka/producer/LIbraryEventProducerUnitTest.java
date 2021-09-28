package com.learn.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.kafka.domain.Book;
import com.learn.kafka.domain.LibraryEvent;
import com.learn.kafka.domain.LibraryEventType;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerRecordTest;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.RecordMetadataTest;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.concurrent.ExecutionException;

@ExtendWith(MockitoExtension.class)
public class LIbraryEventProducerUnitTest {
    @Mock
    KafkaTemplate<Integer,String> kafkaTemplate;

    @Spy
    ObjectMapper objectMapper;

    @InjectMocks
    LibraryEventProducer libraryEventProducer;


    @Test
    void sendLibraryEventWithHeaders_failure() throws JsonProcessingException {
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

        SettableListenableFuture future = new SettableListenableFuture();
        future.setException(new RuntimeException("Exception calling kafka"));

        //when
        Mockito.when( kafkaTemplate.send( Mockito.isA( ProducerRecord.class ) ) )
                .thenReturn(future);

        //then
        Assertions.assertThrows( Exception.class, () -> libraryEventProducer.sendLibraryEventWithHeaders(libraryEvent).get() );
    }

    @Test
    void sendLibraryEventWithHeaders_success() throws JsonProcessingException, ExecutionException, InterruptedException {
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

        ProducerRecord<Integer,String> producerRecord = new ProducerRecord<Integer, String>( "library-events",
                libraryEvent.getLibraryEventId(), payload );
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("library-events",1),
                1L,1L,1L,1L,1,1);
        SendResult<Integer,String> sendResult = new SendResult<Integer, String>(producerRecord,recordMetadata);

        SettableListenableFuture future = new SettableListenableFuture();
        future.set(sendResult);

        //when
        Mockito.when( kafkaTemplate.send( Mockito.isA( ProducerRecord.class ) ) )
                .thenReturn(future);

        ListenableFuture<SendResult<Integer, String>> sendResultListenableFuture = libraryEventProducer.sendLibraryEventWithHeaders(libraryEvent);
        SendResult<Integer, String> result = sendResultListenableFuture.get();

        //then
        Assertions.assertEquals( result.getProducerRecord().value(), payload );
    }
}
