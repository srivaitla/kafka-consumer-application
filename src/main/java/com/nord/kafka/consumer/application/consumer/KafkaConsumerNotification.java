package com.nord.kafka.consumer.application.consumer;

import com.nord.kafka.consumer.application.processor.KafkaConsumerProcessor;
import com.nord.kafka.consumer.application.util.KafkaConsumerLogUtility;
import com.nord.kafka.consumer.dto.ConsumerNotificationRequest;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.nord.kafka.consumer.application.util.KafkaConsumerLogUtility.LOG_NOTIFICATION_NAME;

@Component
public class KafkaConsumerNotification {

    private static final Logger LOGGER = LogManager.getLogger(KafkaConsumerNotification.class);

    @Autowired
    private KafkaConsumerProcessor processor;

    @Autowired
    private KafkaConsumerLogUtility logUtility;

    @Value("${kafka.topic.retry}")
    private String topicName;

    private static final String LOG = "Consumer-";

    @KafkaListener(topics = "${kafka.topic.name.notification}", groupId = "${spring.kafka.consumer.notification.group-id}")
    public void consumer(ConsumerRecord<String, ConsumerNotificationRequest> record) {
        LOGGER.info(LOG + LOG_NOTIFICATION_NAME + " ----- ----- Received : " + logUtility.logConsumerNotification(record) + "\n");

        try {
            processor.process(topicName, LOG_NOTIFICATION_NAME, record.value().getNotificationId().toString(), record.value(), record.headers());
        } catch (Exception ex) {
            LOGGER.error(LOG + LOG_NOTIFICATION_NAME + " ----- ----- Exception : " + logUtility.logConsumerNotification(record, ex) + "\n\n\n");
        }
        LOGGER.info(LOG + LOG_NOTIFICATION_NAME + " ----- ----- Completed : " + logUtility.logConsumerNotification(record) + "\n");
    }
}
