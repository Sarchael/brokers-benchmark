package pl.sarchacode.params;

import pl.sarchacode.Broker;

public class BenchmarkParameters {
  private Broker broker;
  private Boolean brokerOnLocalhost;
  private Integer numberOfProducers;
  private Integer numberOfConsumers;
  private Integer numberOfQueues;
  private Integer messageSize;
  private Integer benchmarkDuration;

  public Broker getBroker() {
    return broker;
  }

  public void setBroker(Broker broker) {
    this.broker = broker;
  }

  public Boolean getBrokerOnLocalhost() {
    return brokerOnLocalhost;
  }

  public void setBrokerOnLocalhost(Boolean brokerOnLocalhost) {
    this.brokerOnLocalhost = brokerOnLocalhost;
  }

  public Integer getNumberOfProducers() {
    return numberOfProducers;
  }

  public void setNumberOfProducers(Integer numberOfProducers) {
    this.numberOfProducers = numberOfProducers;
  }

  public Integer getNumberOfConsumers() {
    return numberOfConsumers;
  }

  public void setNumberOfConsumers(Integer numberOfConsumers) {
    this.numberOfConsumers = numberOfConsumers;
  }

  public Integer getNumberOfQueues() {
    return numberOfQueues;
  }

  public void setNumberOfQueues(Integer numberOfQueues) {
    this.numberOfQueues = numberOfQueues;
  }

  public Integer getMessageSize() {
    return messageSize;
  }

  public void setMessageSize(Integer messageSize) {
    this.messageSize = messageSize;
  }

  public Integer getBenchmarkDuration() {
    return benchmarkDuration;
  }

  public void setBenchmarkDuration(Integer benchmarkDuration) {
    this.benchmarkDuration = benchmarkDuration;
  }
}
