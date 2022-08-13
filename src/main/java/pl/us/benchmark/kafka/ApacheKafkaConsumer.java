package pl.us.benchmark.kafka;

import com.rabbitmq.client.GetResponse;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import pl.us.benchmark.BenchmarkWorkerType;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

public class ApacheKafkaConsumer extends ApacheKafkaWorker {
  private KafkaConsumer<String,String> consumer;
  private Optional<Integer> prefetchCount;

  public ApacheKafkaConsumer(int workerNumber, int topicNumber, Optional<Boolean> brokerOnLocalhost,
                             Optional<Integer> prefetchCount, boolean timeMode) {
    super(BenchmarkWorkerType.CONSUMER, workerNumber, topicNumber, brokerOnLocalhost.orElse(Boolean.FALSE), timeMode);
    this.prefetchCount = prefetchCount;
  }

  @Override
  public void doWork() {
    consumer = new KafkaConsumer<>(brokerOnLocalhost ? ApacheKafkaProperties.getConsumerPropsLocal(prefetchCount)
                                                     : ApacheKafkaProperties.getConsumerPropsRemote(prefetchCount));
    consumer.subscribe(Collections.singletonList(TOPIC_NAME));

    logger.info(THREAD_NAME + ": Connection initialized");
    logger.info(THREAD_NAME + ": Consuming started");

    if (timeMode)
      runInTimeMode();
    else
      runInMessageCountMode();

    run.set(false);
  }

  private void runInTimeMode() {
    while (run.get()) {
      processedMessages.addAndGet(consumer.poll(Duration.ofMillis(10)).count());
    }
  }

  private void runInMessageCountMode() {
    while (run.get()) {
      if (numberOfMessages <= 0) {
        Optional<Integer> pack = messagePool.getPackage();
        if (pack.isPresent())
          numberOfMessages += pack.get();
        else
          break;
      }
      int recordsCount = consumer.poll(Duration.ofMillis(10)).count();
      processedMessages.addAndGet(recordsCount);
      numberOfMessages -= recordsCount;
    }
  }
}
