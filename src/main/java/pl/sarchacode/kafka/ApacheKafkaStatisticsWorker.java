package pl.sarchacode.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class ApacheKafkaStatisticsWorker extends TimerTask {
  private long messagesProduced = 0;
  private long messagesConsumed = 0;

  private List<ApacheKafkaWorker> producers;
  private List<ApacheKafkaWorker> consumers;

  private List<Long> producersStats;
  private List<Long> consumersStats;

  public ApacheKafkaStatisticsWorker(List<ApacheKafkaWorker> producers,
                                     List<ApacheKafkaWorker> consumers) {
    this.producers = producers;
    this.consumers = consumers;
    this.producersStats = new ArrayList<>();
    this.consumersStats = new ArrayList<>();
  }

  @Override
  public void run() {
    long currentMessagesProduced = producers.stream().mapToLong(ApacheKafkaWorker::getProcessedMessages).sum();
    long currentMessagesConsumed = consumers.stream().mapToLong(ApacheKafkaWorker::getProcessedMessages).sum();
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
