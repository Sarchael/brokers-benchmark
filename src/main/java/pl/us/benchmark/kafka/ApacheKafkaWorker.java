package pl.us.benchmark.kafka;

import pl.us.benchmark.BenchmarkWorkerType;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public abstract class ApacheKafkaWorker extends Thread {
  protected final Logger logger = Logger.getLogger(ApacheKafkaWorker.class.getCanonicalName());

  private final String TOPIC_NAME_PREFIX = "BENCHMARK_TOPIC_";

  protected String TOPIC_NAME;
  protected String THREAD_NAME;

  protected BenchmarkWorkerType type;
  protected Integer workerNumber;
  protected AtomicLong processedMessages;
  protected AtomicBoolean run;
  protected boolean brokerOnLocalhost;

  public abstract void doWork();

  public ApacheKafkaWorker(BenchmarkWorkerType type, int workerNumber, int topicNumber, boolean brokerOnLocalhost) {
    this.TOPIC_NAME = TOPIC_NAME_PREFIX + topicNumber;
    this.workerNumber = workerNumber;
    this.type = type;
    this.brokerOnLocalhost = brokerOnLocalhost;

    this.processedMessages = new AtomicLong(0);
    this.run = new AtomicBoolean(true);
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
}
