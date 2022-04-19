package pl.sarchacode.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import pl.sarchacode.BenchmarkWorkerType;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public abstract class RabbitWorker implements Runnable {
  protected final Logger logger = Logger.getLogger(RabbitWorker.class.getCanonicalName());

  protected final String QUEUE_NAME_PREFIX = "BENCHMARK_QUEUE_";
  protected final String CONNECTION_NAME_PREFIX = "BENCHMARK_CONNECTION_";
  protected String QUEUE_NAME;
  protected String CONNECTION_NAME;
  protected String THREAD_NAME;

  protected BenchmarkWorkerType type;
  protected Integer workerNumber;

  protected Channel channel;
  protected Connection connection;

  private boolean brokerOnLocalhost;

  public abstract void doWork() throws IOException;

  public RabbitWorker(BenchmarkWorkerType type, int workerNumber, int queueNumber, boolean brokerOnLocalhost) {
    this.QUEUE_NAME = QUEUE_NAME_PREFIX + queueNumber;
    this.CONNECTION_NAME = CONNECTION_NAME_PREFIX + workerNumber;
    this.workerNumber = workerNumber;
    this.type = type;
    this.brokerOnLocalhost = brokerOnLocalhost;
  }

  @Override
  public void run() {
    THREAD_NAME = Thread.currentThread().getName();
    try {
      initializeConnection();
      doWork();
      if (type == BenchmarkWorkerType.PRODUCER)
        closeConnection();
    } catch (IOException | TimeoutException e) {
      logger.severe(e.getMessage());
    }
  }

  public void initializeConnection() throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(this.brokerOnLocalhost ? RabbitProperties.HOST_LOCAL : RabbitProperties.HOST);
    factory.setPort(RabbitProperties.PORT);
    factory.setUsername(RabbitProperties.USERNAME);
    factory.setPassword(RabbitProperties.PASSWORD);

    connection = factory.newConnection(CONNECTION_NAME);
    channel = connection.createChannel();
    channel.queueDeclare(QUEUE_NAME, false, false, false, null);

    logger.info(THREAD_NAME + ": Connection initialized");
  }

  public void closeConnection() throws IOException, TimeoutException {
    channel.close();
    connection.close();
    logger.info(THREAD_NAME + ": Connection closed");
  }

  public Integer getWorkerNumber() {
    return workerNumber;
  }
}