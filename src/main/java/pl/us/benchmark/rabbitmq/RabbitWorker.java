package pl.us.benchmark.rabbitmq;

import com.rabbitmq.client.Channel;
import pl.us.benchmark.BenchmarkWorkerType;
import pl.us.benchmark.MessagePool;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public abstract class RabbitWorker extends Thread {
  protected final Logger logger = Logger.getLogger(RabbitWorker.class.getCanonicalName());

  private final String QUEUE_NAME_PREFIX = "BENCHMARK_QUEUE_";

  protected String QUEUE_NAME;
  protected String THREAD_NAME;

  protected Integer numberOfMessages;
  protected MessagePool messagePool;
  protected boolean timeMode;
  protected BenchmarkWorkerType type;
  protected Integer workerNumber;
  protected AtomicLong processedMessages;
  protected AtomicBoolean run;

  protected Channel channel;

  private boolean brokerOnLocalhost;

  public abstract void doWork() throws IOException;

  public RabbitWorker(BenchmarkWorkerType type, int workerNumber, int queueNumber,
                      boolean brokerOnLocalhost, boolean timeMode) {
    this.QUEUE_NAME = QUEUE_NAME_PREFIX + queueNumber;
    this.workerNumber = workerNumber;
    this.type = type;
    this.brokerOnLocalhost = brokerOnLocalhost;

    this.processedMessages = new AtomicLong(0);
    this.run = new AtomicBoolean(true);

    this.timeMode = timeMode;
    this.messagePool = MessagePool.getInstance();
    this.numberOfMessages = 0;
  }

  @Override
  public void run() {
    THREAD_NAME = Thread.currentThread().getName();
    try {
      channel = RabbitConnectionFactory.getInstance()
                                       .createChannel(QUEUE_NAME,
                                                      workerNumber.toString(),
                                                      brokerOnLocalhost);
      logger.info(THREAD_NAME + ": Connection initialized");
      doWork();
      if (type == BenchmarkWorkerType.PRODUCER)
        closeConnection();
    } catch (IOException | TimeoutException e) {
      logger.severe(e.getMessage());
    }
  }

  public void closeConnection() throws IOException, TimeoutException {
    RabbitConnectionFactory.getInstance().closeConnection(channel);
    logger.info(THREAD_NAME + ": Connection closed");
  }

  public void stopWorker() {
    run.set(false);
  }

  public Integer getWorkerNumber() {
    return workerNumber;
  }

  public Long getProcessedMessages() {
    return processedMessages.get();
  }
}