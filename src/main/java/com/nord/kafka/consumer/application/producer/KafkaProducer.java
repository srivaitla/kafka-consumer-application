package com.nord.kafka.consumer.application.producer;

import com.nord.kafka.consumer.application.util.KafkaConsumerLogUtility;
import com.nord.kafka.retry.dto.RetryRequest;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KafkaProducer {

    private static final Logger LOGGER = LogManager.getLogger(KafkaProducer.class);

    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;

    private final KafkaConsumerLogUtility logUtility;

    private static final String LOG = "Producer-";

    @Autowired
    public KafkaProducer(KafkaTemplate<String, SpecificRecordBase> kafkaTemplate,
                         KafkaConsumerLogUtility logUtility) {
        this.kafkaTemplate = kafkaTemplate;
        this.logUtility = logUtility;
    }

    public void publishToTopic(String topicName, String logName, RetryRequest request, List<Header> headers) {
        final ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topicName, null, request.getRetryId().toString(), request, headers);
        LOGGER.info(LOG + logName + " ----- ----- Started : " + logUtility.logProducerRecord(record) + "\n");
        try {
            kafkaTemplate.send(record);
            LOGGER.info(LOG + logName + " ----- ----- Completed : " + logUtility.logProducerRecord(record) + "\n\n\n");
        } catch (Exception ex) {
            LOGGER.info(LOG + logName + " ----- ----- Exception : " + logUtility.logProducerRecord(record, ex) + "\n\n\n");
            throw ex;
        }
    }
}
