package pl.sarchacode.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import pl.sarchacode.BenchmarkWorkerType;

import java.util.Optional;

public class ApacheKafkaProducer extends ApacheKafkaWorker {
  private KafkaProducer<String,String> producer;
  private String MESSAGE;

  public ApacheKafkaProducer(int workerNumber, int topicNumber, String message, Optional<Boolean> brokerOnLocalhost) {
    super(BenchmarkWorkerType.PRODUCER, workerNumber, topicNumber, brokerOnLocalhost.orElse(Boolean.FALSE));
    this.MESSAGE = message;
  }

  @Override
  public void doWork() {
    producer = new KafkaProducer<>(brokerOnLocalhost ? ApacheKafkaProperties.getProducerPropsLocal()
                                                     : ApacheKafkaProperties.getProducerPropsRemote());

    logger.info(THREAD_NAME + ": Connection initialized");
    logger.info(THREAD_NAME + ": Messages are sending");

    while (run.get()) {
      producer.send(new ProducerRecord<>(TOPIC_NAME, "benchmark", MESSAGE));
      processedMessages.incrementAndGet();
    }
  }
}
