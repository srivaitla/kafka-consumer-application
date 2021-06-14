package com.nord.kafka.consumer.application.util;

import com.nord.kafka.consumer.dto.ConsumerNotificationRequest;
import com.nord.kafka.consumer.dto.ConsumerProfileRequest;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerLogUtility {

    public static final String LOG_NOTIFICATION_NAME = "Notification";
    public static final String LOG_NOTIFICATION_REPLY_NAME = "Notification-Reply";
    public static final String LOG_PROFILE_NAME = "Profile";
    public static final String LOG_PROFILE_REPLY_NAME = "Profile-Reply";

    @Autowired
    private KafkaConsumerKafkaUtility utility;

    public String logConsumerNotification(ConsumerRecord<String, ConsumerNotificationRequest> record) {
        return "ConsumerRecord[Topic=" + record.topic() + ", " + record.value()
                + ", Headers=" + utility.getHeadersAsString(record.headers()) + "]";
    }

    public String logConsumerNotification(ConsumerRecord<String, ConsumerNotificationRequest> record, Exception ex) {
        return logConsumerNotification(record) + ", Exception= " + ExceptionUtils.getStackTrace(ex);
    }

    public String logConsumerProfile(ConsumerRecord<String, ConsumerProfileRequest> record) {
        return "ConsumerRecord[Topic=" + record.topic() + ", " + record.value()
                + ", Headers=" + utility.getHeadersAsString(record.headers()) + "]";
    }

    public String logConsumerProfile(ConsumerRecord<String, ConsumerProfileRequest> record, Exception ex) {
        return logConsumerProfile(record) + ", Exception= " + ExceptionUtils.getStackTrace(ex);
    }

    public String logProducerRecord(ProducerRecord<String, SpecificRecordBase> record) {
        return "ProducerRecord[Topic=" + record.topic() + ", " + record.value()
                + ", Headers=" + utility.getHeadersAsString(record.headers()) + "]";
    }

    public String logProducerRecord(ProducerRecord<String, SpecificRecordBase> record, Exception ex) {
        return logProducerRecord(record) + ", Exception= " + ExceptionUtils.getStackTrace(ex);
    }

}
