package pl.us.benchmark.rabbitmq;

import pl.us.benchmark.BenchmarkWorkerType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Consumer;

public class RabbitConsumer extends RabbitWorker {
  private Consumer<String> callback;

  public RabbitConsumer(int workerNumber, int queueNumber, Optional<Boolean> brokerOnLocalhost) {
    super(BenchmarkWorkerType.CONSUMER, workerNumber, queueNumber, brokerOnLocalhost.orElse(Boolean.FALSE));
    this.callback = x -> processedMessages.incrementAndGet();
  }

  @Override
  public void doWork() throws IOException {
    channel.basicConsume(QUEUE_NAME,
                         true,
                         (consumerTag, delivery) -> callback.accept(new String(delivery.getBody(),
                                                                               StandardCharsets.UTF_8)),
                         consumerTag -> { });
    logger.info(THREAD_NAME + ": Consuming started");
  }
}
