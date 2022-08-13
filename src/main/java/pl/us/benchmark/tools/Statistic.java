package pl.us.benchmark.tools;

import pl.us.benchmark.Broker;

public class Statistic {
  private String name;
  private Broker broker;
  private Integer size;
  private Integer queues;
  private Integer producers;
  private Integer consumers;
  private Integer prefetchCount;
  private Integer totalThroughputMessagesIn;
  private Integer queueThroughputMessagesIn;
  private Integer totalThroughputTransferIn;
  private Integer queueThroughputTransferIn;
  private Integer totalThroughputMessagesOut;
  private Integer queueThroughputMessagesOut;
  private Integer totalThroughputTransferOut;
  private Integer queueThroughputTransferOut;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Broker getBroker() {
    return broker;
  }

  public void setBroker(Broker broker) {
    this.broker = broker;
  }

  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }

  public Integer getQueues() {
    return queues;
  }

  public void setQueues(Integer queues) {
    this.queues = queues;
  }

  public Integer getProducers() {
    return producers;
  }

  public void setProducers(Integer producers) {
    this.producers = producers;
  }

  public Integer getConsumers() {
    return consumers;
  }

  public void setConsumers(Integer consumers) {
    this.consumers = consumers;
  }

  public Integer getPrefetchCount() {
    return prefetchCount;
  }

  public void setPrefetchCount(Integer prefetchCount) {
    this.prefetchCount = prefetchCount;
  }

  public Integer getTotalThroughputMessagesIn() {
    return totalThroughputMessagesIn;
  }

  public void setTotalThroughputMessagesIn(Integer totalThroughputMessagesIn) {
    this.totalThroughputMessagesIn = totalThroughputMessagesIn;
  }

  public Integer getQueueThroughputMessagesIn() {
    return queueThroughputMessagesIn;
  }

  public void setQueueThroughputMessagesIn(Integer queueThroughputMessagesIn) {
    this.queueThroughputMessagesIn = queueThroughputMessagesIn;
  }

  public Integer getTotalThroughputTransferIn() {
    return totalThroughputTransferIn;
  }

  public void setTotalThroughputTransferIn(Integer totalThroughputTransferIn) {
    this.totalThroughputTransferIn = totalThroughputTransferIn;
  }

  public Integer getQueueThroughputTransferIn() {
    return queueThroughputTransferIn;
  }

  public void setQueueThroughputTransferIn(Integer queueThroughputTransferIn) {
    this.queueThroughputTransferIn = queueThroughputTransferIn;
  }

  public Integer getTotalThroughputMessagesOut() {
    return totalThroughputMessagesOut;
  }

  public void setTotalThroughputMessagesOut(Integer totalThroughputMessagesOut) {
    this.totalThroughputMessagesOut = totalThroughputMessagesOut;
  }

  public Integer getQueueThroughputMessagesOut() {
    return queueThroughputMessagesOut;
  }

  public void setQueueThroughputMessagesOut(Integer queueThroughputMessagesOut) {
    this.queueThroughputMessagesOut = queueThroughputMessagesOut;
  }

  public Integer getTotalThroughputTransferOut() {
    return totalThroughputTransferOut;
  }

  public void setTotalThroughputTransferOut(Integer totalThroughputTransferOut) {
    this.totalThroughputTransferOut = totalThroughputTransferOut;
  }

  public Integer getQueueThroughputTransferOut() {
    return queueThroughputTransferOut;
  }

  public void setQueueThroughputTransferOut(Integer queueThroughputTransferOut) {
    this.queueThroughputTransferOut = queueThroughputTransferOut;
  }
}
