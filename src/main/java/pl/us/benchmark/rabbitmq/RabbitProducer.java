package pl.us.benchmark.rabbitmq;

import pl.us.benchmark.BenchmarkWorkerType;

import java.io.IOException;
import java.util.Optional;

public class RabbitProducer extends RabbitWorker {
  private String MESSAGE;

  public RabbitProducer(int workerNumber, int queueNumber, String message, Optional<Boolean> brokerOnLocalhost,
                        boolean timeMode) {
    super(BenchmarkWorkerType.PRODUCER, workerNumber, queueNumber, brokerOnLocalhost.orElse(Boolean.FALSE), timeMode);
    this.MESSAGE = message;
  }

  @Override
  public void doWork() throws IOException {
    logger.info(THREAD_NAME + ": Messages are sending");

    if (timeMode)
      while (run.get()) {
        channel.basicPublish("", QUEUE_NAME, null, MESSAGE.getBytes());
        processedMessages.incrementAndGet();
      }
    else
      while (run.get()) {
        if (numberOfMessages <= 0) {
          Optional<Integer> pack = messagePool.getPackage();
          if (pack.isPresent())
            numberOfMessages += pack.get();
          else
            break;
        }
        channel.basicPublish("", QUEUE_NAME, null, MESSAGE.getBytes());
        processedMessages.incrementAndGet();
        numberOfMessages--;
      }
    run.set(false);
  }
}
