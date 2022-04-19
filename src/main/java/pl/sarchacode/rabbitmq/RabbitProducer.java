package pl.sarchacode.rabbitmq;

import pl.sarchacode.BenchmarkWorkerType;

import java.io.IOException;
import java.util.Optional;

public class RabbitProducer extends RabbitWorker {
  private String MESSAGE;

  public RabbitProducer(int workerNumber, int queueNumber, String message, Optional<Boolean> brokerOnLocalhost) {
    super(BenchmarkWorkerType.PRODUCER, workerNumber, queueNumber, brokerOnLocalhost.orElse(Boolean.FALSE));
    this.MESSAGE = message;
  }

  @Override
  public void doWork() throws IOException {
    logger.info(THREAD_NAME + ": Messages are sending");

    while (run.get()) {
      channel.basicPublish("", QUEUE_NAME, null, MESSAGE.getBytes());
      processedMessages.incrementAndGet();
    }
  }
}
