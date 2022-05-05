package pl.sarchacode.kafka;

import java.util.Properties;

public class ApacheKafkaProperties {
  private static Properties producerProps;
  private static Properties consumerProps;

  static {
    producerProps = new Properties();
    producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    producerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    producerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

    consumerProps = new Properties();
    consumerProps.put("group.id", "benchmark");
    consumerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    consumerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
  }

  public static Properties getProducerPropsLocal() {
    producerProps.put("bootstrap.servers", "localhost:9093");
    return producerProps;
  }

  public static Properties getProducerPropsRemote() {
    producerProps.put("bootstrap.servers", "192.168.1.31:9093");
    return producerProps;
  }

  public static Properties getConsumerPropsLocal() {
    consumerProps.put("bootstrap.servers", "localhost:9093");
    return consumerProps;
  }

  public static Properties getConsumerPropsRemote() {
    consumerProps.put("bootstrap.servers", "192.168.1.31:9093");
    return consumerProps;
  }
}
