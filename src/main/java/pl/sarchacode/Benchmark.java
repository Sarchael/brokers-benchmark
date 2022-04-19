package pl.sarchacode;

import pl.sarchacode.params.BenchmarkParameters;
import pl.sarchacode.params.BenchmarkParametersParser;
import pl.sarchacode.rabbitmq.*;

import java.util.*;
import java.util.logging.Logger;

public class Benchmark {
  private static final Logger logger = Logger.getLogger(Benchmark.class.getCanonicalName());

  private BenchmarkParameters benchmarkParameters;
  private List<RabbitWorker> producerThreads = new ArrayList<>();
  private List<RabbitWorker> consumerThreads = new ArrayList<>();
  private RabbitStatisticsWorker statisticsWorkerThread;
  private Timer statisticsWorkerTimer;

  public Benchmark(String[] args) {
    benchmarkParameters = BenchmarkParametersParser.parse(Arrays.asList(args));
  }

  public void start() {
    prepareProducerThreads();
    prepareConsumerThreads();
    runProducers();
    runConsumers();
    runStatsProvider();
    if (benchmarkParameters.getBenchmarkDuration() != null)
      runTimeoutTimer();
  }

  private void runTimeoutTimer() {
    Timer timer = new Timer("Timeout");
    TimerTask task = new RabbitTimeoutWorker(producerThreads, consumerThreads, statisticsWorkerThread,
                                             statisticsWorkerTimer, benchmarkParameters);
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.SECOND, benchmarkParameters.getBenchmarkDuration());
    timer.schedule(task, calendar.getTime());
  }

  private void runStatsProvider() {
    Timer timer = new Timer("Statistics");
    RabbitStatisticsWorker task = new RabbitStatisticsWorker(producerThreads, consumerThreads);
    timer.schedule(task, 5000L, 5000L);
    statisticsWorkerThread = task;
    statisticsWorkerTimer = timer;
  }

  private void prepareProducerThreads() {
    int queueNumber = 0;
    for (int i = 0; i < benchmarkParameters.getNumberOfProducers(); i++) {
      producerThreads.add(new RabbitProducer(i + 1,
                                             queueNumber + 1,
                                             prepareMessage(benchmarkParameters.getMessageSize()),
                                             Optional.ofNullable(benchmarkParameters.getBrokerOnLocalhost())));
      if (++queueNumber >= benchmarkParameters.getNumberOfQueues())
        queueNumber = 0;
    }
  }

  private void prepareConsumerThreads() {
    int queueNumber = 0;
    for (int i = 0; i < benchmarkParameters.getNumberOfConsumers(); i++) {
      consumerThreads.add(new RabbitConsumer(i + 1,
                                             queueNumber + 1,
                                             Optional.ofNullable(benchmarkParameters.getBrokerOnLocalhost())));
      if (++queueNumber >= benchmarkParameters.getNumberOfQueues())
        queueNumber = 0;
    }
  }

  private void runProducers() {
    producerThreads.forEach(x -> new Thread(x, "Producer-" + x.getWorkerNumber()).start());
    logger.info("Producers threads are running");
  }

  private void runConsumers() {
    consumerThreads.forEach(x -> new Thread(x, "Consumer-" + x.getWorkerNumber()).start());
    logger.info("Consumers threads are running");
  }

  private String prepareMessage(Integer messageSize) {
    char[] chars = new char[messageSize];
    Arrays.fill(chars, 'x');
    return new String(chars);
  }
}
