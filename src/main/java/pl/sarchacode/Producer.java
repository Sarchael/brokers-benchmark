package pl.sarchacode;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class Producer implements Runnable {

  private static final Logger logger = Logger.getLogger(Producer.class.getCanonicalName());

  private static final String QUEUE_NAME_PREFIX = "BENCHMARK_QUEUE_";
  private static final String CONNECTION_NAME_PREFIX = "BENCHMARK_CONNECTION_";
  private String QUEUE_NAME;
  private String CONNECTION_NAME;
  private String MESSAGE;
  private Integer REPETITIONS;
  private String THREAD_NAME;

  private Integer producerNumber;

  private Channel channel;
  private Connection connection;

  public Producer(int producerNumber, int queueNumber, String message, Integer repetitions) {
    QUEUE_NAME = QUEUE_NAME_PREFIX + queueNumber;
    CONNECTION_NAME = CONNECTION_NAME_PREFIX + producerNumber;
    MESSAGE = message;
    REPETITIONS = repetitions;

    this.producerNumber = producerNumber;
  }

  @Override
  public void run() {
    THREAD_NAME = Thread.currentThread().getName();
    try {
      initializeConnection();
      sendMessages();
      closeConnection();
    } catch (IOException | TimeoutException e) {
      logger.severe(e.getMessage());
    }
  }

  public void initializeConnection() throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(RabbitProperties.HOST);
    factory.setPort(RabbitProperties.PORT);
    factory.setUsername(RabbitProperties.USERNAME);
    factory.setPassword(RabbitProperties.PASSWORD);

    connection = factory.newConnection(CONNECTION_NAME);
    channel = connection.createChannel();
    channel.queueDeclare(QUEUE_NAME, false, false, false, null);

    logger.info(THREAD_NAME + ": Connection initialized");
  }

  public void sendMessages() throws IOException {
    for (int i = 0; i < REPETITIONS; i++)
      channel.basicPublish("", QUEUE_NAME, null, MESSAGE.getBytes());

    logger.info(THREAD_NAME + ": Messages sent");
  }

  public void closeConnection() throws IOException, TimeoutException {
    channel.close();
    connection.close();

    logger.info(THREAD_NAME + ": Connection closed");
  }

  public Integer getProducerNumber() {
    return producerNumber;
  }
}
