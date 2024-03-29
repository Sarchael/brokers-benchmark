package pl.us.benchmark.kafka;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import pl.us.benchmark.BenchmarkWorkerType;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

public class ApacheKafkaConsumer extends ApacheKafkaWorker {
  private KafkaConsumer<String,String> consumer;

  public ApacheKafkaConsumer(int workerNumber, int topicNumber, Optional<Boolean> brokerOnLocalhost, boolean timeMode) {
    super(BenchmarkWorkerType.CONSUMER, workerNumber, topicNumber, brokerOnLocalhost.orElse(Boolean.FALSE), timeMode);
  }

  @Override
  public void doWork() {
    consumer = new KafkaConsumer<>(brokerOnLocalhost ? ApacheKafkaProperties.getConsumerPropsLocal()
                                                     : ApacheKafkaProperties.getConsumerPropsRemote());
    consumer.subscribe(Collections.singletonList(TOPIC_NAME));

    if (timeMode)
      runInTimeMode();
    else
      runInMessageCountMode();
  }

  private void runInTimeMode() {
    while (run.get()) {
      processedMessages.addAndGet(consumer.poll(Duration.ofMillis(500)).count());
    }
    consumer.commitSync();
    consumer.unsubscribe();
    consumer.close();
  }

  private void runInMessageCountMode() {
    this.startTimestamp = System.currentTimeMillis();
    while (true) {
      int recordsCount = consumer.poll(Duration.ofMillis(500)).count();
      processedMessages.addAndGet(recordsCount);
      if (!messagePool.registerPackage(recordsCount))
        break;
    }
    consumer.commitSync();
    this.finishTimestamp = System.currentTimeMillis();
    consumer.unsubscribe();
    consumer.close();
    run.set(false);
  }
}
