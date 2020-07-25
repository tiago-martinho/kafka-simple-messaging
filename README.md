# Kafka Messaging 

This project consists of two simple spring applications, a consumer and a producer, using Spring for Apache Kafka

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

You should have both a zookeeper and cp-kafka instances running before running the consumer and producer.
To avoid installs on the local machine and for ease of use I used docker. Notice that Zookeeper and cp-kafka have to be on the same network for this to work.

First run Zookeeper in docker:

```
 docker container run --name zookeeper -p 2181:2181 --network kafka-network --restart always -d zookeeper
```

And then Kafka from Confluentic (you can test other images if you want):

```
docker run --name kafka -p 9092:9092 --network kafka-network 
-e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 
-e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 
-e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 confluentinc/cp-kafka
```

Create a topic in the Kafka container:

```
docker container exec -it kafka kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic test
```

You can also do this programatically:

```
@Configuration
public class KafkaTopicConfig {
    
    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;
 
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }
    
    @Bean
    public NewTopic topic1() {
         return new NewTopic("test", 1, (short) 1);
    }
}
```

Check that the topic was created successfully:

```
docker container exec -it kafka kafka-topics --list --bootstrap-server localhost:9092
```

This project is a Spring Boot maven project using Java 11. You should have Maven and JDK 11.x.x installed. You can use Oracle's official JDK for personal use [here]( https://www.oracle.com/java/technologies/javase-jdk11-downloads.html).


For the latest version of Maven go [here](https://maven.apache.org/download.cgi)

Get Docker [here](https://www.docker.com/get-started) if you want to use containers

## Build


Build and install depedencies for the both consumer and producer services using

```
mvn clean install
```

## Run


To run both the consumer and producer services:

```
mvn spring-boot:run
```

You can also run another consumer in the docker container and experiment with the same groupId (to check load balancing) or with a different groupId (for broadcast):

```
docker container exec -it kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic test --consumer-property group.id=1
```

Likewise, you can run another producer in the docker container and publish messages from there:

```
docker container exec -it kafka kafka-console-producer --bootstrap-server localhost:9092 --topic test
```

## References

https://kafka.apache.org/ <br>
https://docs.spring.io/spring-kafka/docs/2.5.3.RELEASE/reference/html/ <br>
https://www.baeldung.com/spring-kafka
