package pl.sarchacode.rabbitmq;

import pl.sarchacode.BenchmarkWorkerType;

import java.io.IOException;
import java.util.Optional;

public class RabbitProducer extends RabbitWorker {
  private String MESSAGE;
  private Optional<Integer> REPETITIONS;

  public RabbitProducer(int workerNumber, int queueNumber, String message, Optional<Integer> repetitions) {
    super(BenchmarkWorkerType.PRODUCER, workerNumber, queueNumber);
    this.MESSAGE = message;
    this.REPETITIONS = repetitions;
  }

  @Override
  public void doWork() throws IOException {
    if (REPETITIONS.isPresent()) {
      for (int i = 0; i < REPETITIONS.get(); i++)
        channel.basicPublish("", QUEUE_NAME, null, MESSAGE.getBytes());

      logger.info(THREAD_NAME + ": All messages sent");
    } else {
      logger.info(THREAD_NAME + ": Infinity sending mode active. Messages are sending");

      while (true)
        channel.basicPublish("", QUEUE_NAME, null, MESSAGE.getBytes());
    }
  }
}
