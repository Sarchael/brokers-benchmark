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

    logger.info(THREAD_NAME + ": Connection initialized");
    logger.info(THREAD_NAME + ": Messages are sending");

    if (timeMode)
      while (run.get()) {
        producer.send(new ProducerRecord<>(TOPIC_NAME, "benchmark", MESSAGE));
        processedMessages.incrementAndGet();
      }
    else
      while (run.get()) {
        if (numberOfMessages == 0) {
          Optional<Integer> pack = messagePool.getPackage();
          if (pack.isPresent())
            numberOfMessages = pack.get();
          else
            break;
        }
        producer.send(new ProducerRecord<>(TOPIC_NAME, "benchmark", MESSAGE));
        processedMessages.incrementAndGet();
        numberOfMessages--;
      }
    run.set(false);
  }
}
