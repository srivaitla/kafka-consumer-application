package com.nord.kafka.consumer.application.consumer;

import com.nord.kafka.consumer.application.processor.KafkaConsumerProcessor;
import com.nord.kafka.consumer.application.util.KafkaConsumerLogUtility;
import com.nord.kafka.consumer.dto.ConsumerProfileRequest;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.nord.kafka.consumer.application.util.KafkaConsumerLogUtility.LOG_PROFILE_REPLY_NAME;

@Component
public class KafkaConsumerProfileReply {

    private static final Logger LOGGER = LogManager.getLogger(KafkaConsumerProfileReply.class);

    @Autowired
    private KafkaConsumerProcessor processor;

    @Autowired
    private KafkaConsumerLogUtility logUtility;

    @Value("${kafka.topic.retry}")
    private String topicName;

    private static final String LOG = "Consumer-";

    @KafkaListener(topics = "${kafka.topic.name.profile.reply}", groupId = "${spring.kafka.consumer.profile.reply.group-id}")
    public void consume(ConsumerRecord<String, ConsumerProfileRequest> record) {
        LOGGER.info(LOG + LOG_PROFILE_REPLY_NAME + " ----- ----- Received : " + logUtility.logConsumerProfile(record) + "\n");

        try {
            processor.process(topicName, LOG_PROFILE_REPLY_NAME, record.value().getProfileId().toString(), record.value(), record.headers());
        } catch (Exception ex) {
            LOGGER.error(LOG + LOG_PROFILE_REPLY_NAME + " ----- ----- Exception : " + logUtility.logConsumerProfile(record, ex) + "\n\n\n");
        }
        LOGGER.info(LOG + LOG_PROFILE_REPLY_NAME + " ----- ----- Completed : " + logUtility.logConsumerProfile(record) + "\n");
    }
}
