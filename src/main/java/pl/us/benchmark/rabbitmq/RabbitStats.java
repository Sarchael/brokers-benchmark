package pl.us.benchmark.rabbitmq;

public class RabbitStats {
  private Long publishedMessages;
  private Long consumedMessages;

  public Long getPublishedMessages() {
    return publishedMessages;
  }

  public void setPublishedMessages(Long publishedMessages) {
    this.publishedMessages = publishedMessages;
  }

  public Long getConsumedMessages() {
    return consumedMessages;
  }

  public void setConsumedMessages(Long consumedMessages) {
    this.consumedMessages = consumedMessages;
  }
}
