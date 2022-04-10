package pl.sarchacode.params;

public class BenchmarkParameters {
  private Integer numberOfProducers;
  private Integer numberOfConsumers;
  private Integer numberOfQueues;
  private Integer numberOfMessages;
  private Integer messageSize;

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

  public Integer getNumberOfMessages() {
    return numberOfMessages;
  }

  public void setNumberOfMessages(Integer numberOfMessages) {
    this.numberOfMessages = numberOfMessages;
  }

  public Integer getMessageSize() {
    return messageSize;
  }

  public void setMessageSize(Integer messageSize) {
    this.messageSize = messageSize;
  }
}
