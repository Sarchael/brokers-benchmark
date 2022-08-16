package pl.us.benchmark.rabbitmq;

import pl.us.benchmark.BenchmarkWorkerType;

import java.io.IOException;
import java.util.Optional;

public class RabbitConsumer extends RabbitWorker {

  public RabbitConsumer(int workerNumber, int queueNumber, Optional<Boolean> brokerOnLocalhost, boolean timeMode) {
    super(BenchmarkWorkerType.CONSUMER, workerNumber, queueNumber, brokerOnLocalhost.orElse(Boolean.FALSE), timeMode);
  }

  @Override
  public void doWork() throws IOException {
    channel.basicConsume(QUEUE_NAME,
                         true,
                         (consumerTag, delivery) -> processedMessages.incrementAndGet(),
                         consumerTag -> { });
  }
}
