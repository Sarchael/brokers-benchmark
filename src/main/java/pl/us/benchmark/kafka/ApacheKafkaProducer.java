package pl.us.benchmark.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import pl.us.benchmark.BenchmarkWorkerType;

import java.util.Optional;

public class ApacheKafkaProducer extends ApacheKafkaWorker {
  private KafkaProducer<String,String> producer;
  private String MESSAGE;

  public ApacheKafkaProducer(int workerNumber, int topicNumber, String message, Optional<Boolean> brokerOnLocalhost,
                             boolean timeMode) {
    super(BenchmarkWorkerType.PRODUCER, workerNumber, topicNumber, brokerOnLocalhost.orElse(Boolean.FALSE), timeMode);
    this.MESSAGE = message;
  }

  @Override
  public void doWork() {
    producer = new KafkaProducer<>(brokerOnLocalhost ? ApacheKafkaProperties.getProducerPropsLocal()
                                                     : ApacheKafkaProperties.getProducerPropsRemote());

    if (timeMode)
      runInTimeMode();
    else
      runInMessageCountMode();
  }

  private void runInTimeMode() {
    while (run.get()) {
      producer.send(new ProducerRecord<>(TOPIC_NAME, null, MESSAGE));
      processedMessages.incrementAndGet();
    }
    producer.flush();
  }

  private void runInMessageCountMode() {
    while (true) {
      if (numberOfMessages == 0) {
        Optional<Integer> pack = messagePool.getPackage();
        if (pack.isPresent())
          numberOfMessages = pack.get();
        else
          break;
      }
      producer.send(new ProducerRecord<>(TOPIC_NAME, null, MESSAGE));
      processedMessages.incrementAndGet();
      numberOfMessages--;
    }
    producer.flush();
    run.set(false);
  }
}
