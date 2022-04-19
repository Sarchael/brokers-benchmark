package pl.sarchacode.rabbitmq;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class RabbitStatisticsWorker extends TimerTask {
  private long messagesProduced = 0;
  private long messagesConsumed = 0;

  private List<RabbitWorker> producers;
  private List<RabbitWorker> consumers;

  private List<Long> producersStats;
  private List<Long> consumersStats;

  public RabbitStatisticsWorker(List<RabbitWorker> producers,
                                List<RabbitWorker> consumers) {
    this.producers = producers;
    this.consumers = consumers;
    this.producersStats = new ArrayList<>();
    this.consumersStats = new ArrayList<>();
  }

  @Override
  public void run() {
    long currentMessagesProduced = producers.stream().mapToLong(RabbitWorker::getProcessedMessages).sum();
    long currentMessagesConsumed = consumers.stream().mapToLong(RabbitWorker::getProcessedMessages).sum();
    long producingDelta = (currentMessagesProduced - messagesProduced) / 5;
    long consumingDelta = (currentMessagesConsumed - messagesConsumed) / 5;
    messagesProduced = currentMessagesProduced;
    messagesConsumed = currentMessagesConsumed;
    producersStats.add(producingDelta);
    consumersStats.add(consumingDelta);
  }

  public List<Long> getProducersStats() {
    return producersStats;
  }

  public List<Long> getConsumersStats() {
    return consumersStats;
  }
}
