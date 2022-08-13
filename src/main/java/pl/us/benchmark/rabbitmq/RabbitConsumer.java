package pl.us.benchmark.rabbitmq;

import com.rabbitmq.client.GetResponse;
import pl.us.benchmark.BenchmarkWorkerType;

import java.io.IOException;
import java.util.Optional;

public class RabbitConsumer extends RabbitWorker {
  private Optional<Integer> prefetchCount;

  public RabbitConsumer(int workerNumber, int queueNumber, Optional<Boolean> brokerOnLocalhost,
                        Optional<Integer> prefetchCount, boolean timeMode) {
    super(BenchmarkWorkerType.CONSUMER, workerNumber, queueNumber, brokerOnLocalhost.orElse(Boolean.FALSE), timeMode);
    this.prefetchCount = prefetchCount;
  }

  @Override
  public void doWork() throws IOException {
    logger.info(THREAD_NAME + ": Consuming started");

    if (prefetchCount.isPresent() && !timeMode)
      runWithPrefetchInMessageCountMode();
    else if (prefetchCount.isEmpty() && !timeMode)
      runWithoutPrefetchInMessageCountMode();
    else if (prefetchCount.isPresent())
      runWithPrefetchInTimeMode();
    else
      runWithoutPrefetchInTimeMode();
    run.set(false);
  }

  private void runWithPrefetchInTimeMode() throws IOException {
    channel.basicQos(prefetchCount.get());

    while (run.get()) {
      GetResponse response = channel.basicGet(QUEUE_NAME, false);
      channel.basicAck(response.getEnvelope().getDeliveryTag(), true);
      processedMessages.addAndGet(response.getMessageCount());
    }
  }

  private void runWithPrefetchInMessageCountMode() throws IOException {
    channel.basicQos(prefetchCount.get());

    while (run.get()) {
      if (numberOfMessages <= 0) {
        Optional<Integer> pack = messagePool.getPackage();
        if (pack.isPresent())
          numberOfMessages += pack.get();
        else
          break;
      }
      GetResponse response = channel.basicGet(QUEUE_NAME, false);
      channel.basicAck(response.getEnvelope().getDeliveryTag(), true);
      processedMessages.addAndGet(response.getMessageCount());
      numberOfMessages -= response.getMessageCount();
    }
  }

  private void runWithoutPrefetchInTimeMode() throws IOException {
    while (run.get()) {
      GetResponse response = channel.basicGet(QUEUE_NAME, true);
      processedMessages.addAndGet(response.getMessageCount());
    }
  }

  private void runWithoutPrefetchInMessageCountMode() throws IOException {
    while (run.get()) {
      if (numberOfMessages <= 0) {
        Optional<Integer> pack = messagePool.getPackage();
        if (pack.isPresent())
          numberOfMessages += pack.get();
        else
          break;
      }
      GetResponse response = channel.basicGet(QUEUE_NAME, true);
      processedMessages.addAndGet(response.getMessageCount());
      numberOfMessages -= response.getMessageCount();
    }
  }
}
