# kafka-consumer-application
- It's Spring boot REST service.
- It consumes the events from consumer's kafka topic, which is published by 'kafka-client-application'.
- It converts consumer's AVRO object to retry AVRO Object.
- It publishes this retry AVRO object to retry kafka topic, which is consumed by 'kafka-retry-application'.

## Pre-Requisite:

- Install Zookeeper, Kafka and Schema Register in Local/Test. Steps are given in 'Kafka Setup' section.
- Register schemas which are under 'src/main/schema/avro/' folder for your topics (name is given in the application.properties file).

## Update Configuration
- Based on environment, we need to update following field values in pom.xml file.
  - Schema_Registry_URL --> Get_Schema_Registry_URL_From_Application_Properties_File_Based_On_Environment
      - For Local, value will be
          - http://localhost:8082

  - Schema_Registry_URL_UserName_Password --> userName:password
    - For Local, no need to change.
    - For Test, need to update.


## Commands to set up Kafka Topics for this project:

        ./bin/kafka-topics --create --bootstrap-server localhost:9092 --topic consumer-notification-request

        ./bin/kafka-topics --create --bootstrap-server localhost:9092 --topic consumer-profile-request

        ./bin/kafka-topics --create --bootstrap-server localhost:9092 --topic retry-request-avro


## Commands to set up Kafka Schemas for this project:

        jq '. | {schema: tojson}' ~/Source/git/git_learn/kafka-consumer-application/schema/avro/consumer-notification-request.avsc | curl -i -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" http://localhost:8082/subjects/consumer-notification-request-value/versions -d @-

        jq '. | {schema: tojson}' ~/Source/git/git_learn/kafka-consumer-application/schema/avro/consumer-profile-request.avsc | curl -i -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" http://localhost:8082/subjects/consumer-profile-request-value/versions -d @-

        jq '. | {schema: tojson}' ~/Source/git/git_learn/kafka-consumer-application/schema/avro/retry-request-avro.avsc | curl -i -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" http://localhost:8082/subjects/retry-request-avro-value/versions -d @-


## Setup 'Kafka' in Local:

A detailed documentation is listed here: https://github.com/srivaitla/kafka-rest-application/blob/master/README.md