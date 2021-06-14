package com.nord.kafka.consumer.application.util;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class KafkaConsumerKafkaUtility {

    @Autowired
    private KafkaConsumerLogUtility logUtility;

    public String getHeadersAsString(Headers recordHeaders) {
        final StringBuilder headersBuilder = new StringBuilder().append('{');
        for (Header header : recordHeaders) {
            headersBuilder.append(header.key()).append('=').append(new String(header.value(), StandardCharsets.UTF_8)).append("; ");
        }
        headersBuilder.append('}');
        return headersBuilder.toString();
    }

    public static byte[] serialize(SpecificRecordBase t) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        final DatumWriter<SpecificRecordBase> writer = new SpecificDatumWriter<>(t.getSchema());
        writer.write(t, encoder);
        encoder.flush();
        out.close();
        return out.toByteArray();
    }
}
