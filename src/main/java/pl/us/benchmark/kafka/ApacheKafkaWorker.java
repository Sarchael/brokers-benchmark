package pl.us.benchmark.kafka;

import pl.us.benchmark.BenchmarkWorkerType;
import pl.us.benchmark.MessagePool;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public abstract class ApacheKafkaWorker extends Thread {

  private final String TOPIC_NAME_PREFIX = "BENCHMARK_TOPIC_";

  protected String TOPIC_NAME;
  protected String THREAD_NAME;

  protected Integer numberOfMessages;
  protected MessagePool messagePool;
  protected boolean timeMode;
  protected BenchmarkWorkerType type;
  protected Integer workerNumber;
  protected AtomicLong processedMessages;
  protected AtomicBoolean run;
  protected boolean brokerOnLocalhost;

  protected long startTimestamp;
  protected long finishTimestamp;

  public abstract void doWork();

  public ApacheKafkaWorker(BenchmarkWorkerType type, int workerNumber, int topicNumber,
                           boolean brokerOnLocalhost, boolean timeMode) {
    this.TOPIC_NAME = TOPIC_NAME_PREFIX + topicNumber;
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
    doWork();
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
