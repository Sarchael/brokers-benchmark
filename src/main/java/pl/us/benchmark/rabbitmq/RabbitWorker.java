package pl.us.benchmark.rabbitmq;

import com.rabbitmq.client.Channel;
import pl.us.benchmark.BenchmarkWorkerType;
import pl.us.benchmark.MessagePool;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public abstract class RabbitWorker extends Thread {

  private final String QUEUE_NAME_PREFIX = "BENCHMARK_QUEUE_";

  protected String QUEUE_NAME;
  protected String THREAD_NAME;

  protected AtomicLong numberOfMessages;
  protected MessagePool messagePool;
  protected boolean timeMode;
  protected BenchmarkWorkerType type;
  protected Integer workerNumber;
  protected AtomicLong processedMessages;
  protected AtomicBoolean run;

  protected Channel channel;

  private boolean brokerOnLocalhost;

  protected long startTimestamp;
  protected long finishTimestamp;

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
    this.numberOfMessages = new AtomicLong(0);
  }

  @Override
  public void run() {
    THREAD_NAME = Thread.currentThread().getName();
    try {
      channel = RabbitConnectionFactory.getInstance(brokerOnLocalhost)
                                       .createChannel(QUEUE_NAME,
                                                      workerNumber.toString());
      doWork();
    } catch (IOException | TimeoutException e) {
      e.printStackTrace();
    }
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

  public long getStartTimestamp() {
    return startTimestamp;
  }

  public long getFinishTimestamp() {
    return finishTimestamp;
  }

  public boolean isRunning() {
    return run.get();
  }
}