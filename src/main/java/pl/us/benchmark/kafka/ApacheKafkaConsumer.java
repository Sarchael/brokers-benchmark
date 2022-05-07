package pl.us.benchmark.kafka;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import pl.us.benchmark.BenchmarkWorkerType;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

public class ApacheKafkaConsumer extends ApacheKafkaWorker {
  private KafkaConsumer<String,String> consumer;

  public ApacheKafkaConsumer(int workerNumber, int topicNumber, Optional<Boolean> brokerOnLocalhost) {
    super(BenchmarkWorkerType.CONSUMER, workerNumber, topicNumber, brokerOnLocalhost.orElse(Boolean.FALSE));
  }

  @Override
  public void doWork() {
    consumer = new KafkaConsumer<>(brokerOnLocalhost ? ApacheKafkaProperties.getConsumerPropsLocal()
                                                     : ApacheKafkaProperties.getConsumerPropsRemote());
    consumer.subscribe(Collections.singletonList(TOPIC_NAME));

    logger.info(THREAD_NAME + ": Connection initialized");
    logger.info(THREAD_NAME + ": Consuming started");

    while (run.get()) {
      processedMessages.addAndGet(consumer.poll(Duration.ofMillis(10)).count());
    }
  }
}
