package com.nord.kafka.consumer.application.processor;

import com.nord.kafka.consumer.application.producer.KafkaProducer;
import com.nord.kafka.consumer.application.util.KafkaConsumerKafkaUtility;
import com.nord.kafka.retry.dto.ConsumerRecord;
import com.nord.kafka.retry.dto.PayloadRecord;
import com.nord.kafka.retry.dto.RetryRequest;
import com.nord.kafka.retry.dto.SourceRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nord.kafka.consumer.application.util.KafkaConsumerLogUtility.LOG_NOTIFICATION_NAME;
import static com.nord.kafka.consumer.application.util.KafkaConsumerLogUtility.LOG_NOTIFICATION_REPLY_NAME;

@Service
public class KafkaConsumerProcessor {

    private static final Logger LOGGER = LogManager.getLogger(KafkaConsumerProcessor.class);

    private final static String RETRY_COUNT = "retryCount";
    private final static String RETRY_TIME = "retryTime";

    @Autowired
    private KafkaProducer producer;

    @Value("${kafka.topic.name.notification.reply}")
    private String topicConsumerNotification;

    @Value("${kafka.topic.name.profile.reply}")
    private String topicConsumerProfile;

    private static final String LOG = "Processor-";

    public void process(String topicName, String logName, String requestId, SpecificRecordBase request, Headers headers) throws IOException {
        final RetryRequest retryRequest = buildRetryRequest(request, headers, requestId, logName);
        LOGGER.info(LOG + logName + " ----- ----- Started : " + request + "\n");

        producer.publishToTopic(topicName, logName, retryRequest, buildHeaders(headers));

        LOGGER.info(LOG + logName + " ----- ----- Completed : " + request + "\n");
    }

    private RetryRequest buildRetryRequest(SpecificRecordBase request, Headers headers,
                                           CharSequence requestId, String logName) throws IOException {
        final RetryRequest retryRequest = new RetryRequest();
        retryRequest.setRetryId(requestId);
        retryRequest.setPayloadRecord(buildPayload(request));
        if (LOG_NOTIFICATION_NAME.equals(logName) || LOG_NOTIFICATION_REPLY_NAME.equals(logName)) {
            retryRequest.setConsumerRecord(buildConsumerNotification());
        } else {
            retryRequest.setConsumerRecord(buildConsumerProfile());
        }
        retryRequest.setSourceRecord(buildSourceRecord(headers));
        return retryRequest;
    }

    private PayloadRecord buildPayload(SpecificRecordBase request) throws IOException {
        final byte[] requestPayload = KafkaConsumerKafkaUtility.serialize(request);
        final PayloadRecord payload = new PayloadRecord();
        payload.setPayload(ByteBuffer.wrap(requestPayload));
        payload.setPayloadClassName(request.getClass().getName());
        return payload;
    }

    private ConsumerRecord buildConsumerNotification() {
        final ConsumerRecord consumer = new ConsumerRecord();
        consumer.setName("ConsumerNotification");
        consumer.setReplyTopic(topicConsumerNotification);
        consumer.setEmailAddress("notification@gmail.com");
        consumer.setSlackUrl("notificationSlack");
        return consumer;
    }

    private ConsumerRecord buildConsumerProfile() {
        final ConsumerRecord consumer = new ConsumerRecord();
        consumer.setName("ConsumerProfile");
        consumer.setReplyTopic(topicConsumerProfile);
        consumer.setEmailAddress("profile@gmail.com");
        consumer.setSlackUrl("profileSlack");
        return consumer;
    }

    private SourceRecord buildSourceRecord(Headers headers) {
        final SourceRecord source = new SourceRecord();
        source.setName("source");
        final Map<CharSequence, CharSequence> sourceHeaders = new HashMap<>();
        for (Header header : headers) {
            sourceHeaders.put(header.key(), new String(header.value(), StandardCharsets.UTF_8));
        }
        source.setHeaders(sourceHeaders);
        return source;
    }

    private List<Header> buildHeaders(Headers retryHeaders) {
        final List<Header> headers = new ArrayList<>();
        headers.add(new RecordHeader("ID", "100".getBytes(StandardCharsets.UTF_8)));
        headers.add(new RecordHeader("AppId", "EAS".getBytes(StandardCharsets.UTF_8)));
        for (Header header : retryHeaders) {
            if (RETRY_COUNT.equals(header.key()) || RETRY_TIME.equals(header.key())) {
                headers.add(new RecordHeader(header.key(), header.value()));
            }
        }
        return headers;
    }
}
